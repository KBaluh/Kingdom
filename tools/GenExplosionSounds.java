import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Random;

/**
 * Procedurally synthesizes underwater rocket explosion sounds (16-bit mono WAV).
 *
 * Run from the project root:
 *   java tools/GenExplosionSounds.java [outputDir]
 *
 * Each sound is a mix of:
 *   - a short high-passed noise "crack" (the detonation transient)
 *   - a sine "boom" sweeping down from ~130 Hz to ~35 Hz
 *   - a noise body whose low-pass cutoff closes over time (muffled by water)
 *   - a slow low rumble tail
 *   - a handful of rising sine chirps (bubbles)
 */
public class GenExplosionSounds {

    private static final int SAMPLE_RATE = 44100;
    private static final int VARIANTS = 4;

    public static void main(String[] args) throws Exception {
        File outDir = new File(args.length > 0 ? args[0] : "godot/assets/sounds");
        if (!outDir.isDirectory() && !outDir.mkdirs()) {
            throw new IllegalStateException("Cannot create " + outDir);
        }
        for (int i = 1; i <= VARIANTS; i++) {
            double[] samples = explosion(new Random(i * 0x9E3779B97F4A7C15L));
            File file = new File(outDir, "rocket_explosion" + i + ".wav");
            write(samples, file);
            System.out.printf("%s  %.2fs%n", file.getPath(), samples.length / (double) SAMPLE_RATE);
        }
    }

    private static double[] explosion(Random rnd) {
        double duration = 1.1 + rnd.nextDouble() * 0.4;
        int n = (int) (duration * SAMPLE_RATE);
        double[] out = new double[n];

        // Boom: downward sine sweep
        double fStart = 110 + rnd.nextDouble() * 50;
        double fEnd = 32 + rnd.nextDouble() * 10;
        double sweepRate = 9 + rnd.nextDouble() * 5;
        double boomDecay = 4.5 + rnd.nextDouble() * 2;
        double phase = 0;
        for (int i = 0; i < n; i++) {
            double t = i / (double) SAMPLE_RATE;
            double f = fEnd + (fStart - fEnd) * Math.exp(-sweepRate * t);
            phase += 2 * Math.PI * f / SAMPLE_RATE;
            double attack = Math.min(1, t / 0.003);
            out[i] += 0.9 * Math.sin(phase) * Math.exp(-boomDecay * t) * attack;
        }

        // Noise body (closing low-pass), crack (high-passed), rumble (very low)
        double bodyDecay = 5 + rnd.nextDouble() * 2;
        double cutoffFall = 7 + rnd.nextDouble() * 4;
        double lp1 = 0, lp2 = 0, crackLp = 0, rumble1 = 0, rumble2 = 0;
        double rumbleA = 1 - Math.exp(-2 * Math.PI * 110 / SAMPLE_RATE);
        double crackA = 1 - Math.exp(-2 * Math.PI * 1800.0 / SAMPLE_RATE);
        double wobbleHz = 5 + rnd.nextDouble() * 4;
        for (int i = 0; i < n; i++) {
            double t = i / (double) SAMPLE_RATE;
            double white = rnd.nextDouble() * 2 - 1;

            double cutoff = 180 + 3800 * Math.exp(-cutoffFall * t);
            double a = 1 - Math.exp(-2 * Math.PI * cutoff / SAMPLE_RATE);
            lp1 += a * (white - lp1);
            lp2 += a * (lp1 - lp2);
            double attack = Math.min(1, t / 0.002);
            out[i] += 1.6 * lp2 * Math.exp(-bodyDecay * t) * attack;

            crackLp += crackA * (white - crackLp);
            out[i] += 0.45 * (white - crackLp) * Math.exp(-150 * t);

            rumble1 += rumbleA * (white - rumble1);
            rumble2 += rumbleA * (rumble1 - rumble2);
            double wobble = 0.65 + 0.35 * Math.sin(2 * Math.PI * wobbleHz * t);
            out[i] += 5.0 * rumble2 * wobble * Math.exp(-2.6 * t) * Math.min(1, t / 0.03);
        }

        // Bubbles: short rising chirps scattered after the blast
        int bubbles = 10 + rnd.nextInt(9);
        for (int b = 0; b < bubbles; b++) {
            double start = 0.08 + rnd.nextDouble() * (duration - 0.35);
            double len = 0.03 + rnd.nextDouble() * 0.045;
            double f0 = 380 + rnd.nextDouble() * 1000;
            double rise = 1.3 + rnd.nextDouble() * 0.6;
            double amp = (0.05 + rnd.nextDouble() * 0.09) * Math.exp(-1.8 * start);
            int from = (int) (start * SAMPLE_RATE);
            int count = (int) (len * SAMPLE_RATE);
            double ph = 0;
            for (int i = 0; i < count && from + i < n; i++) {
                double x = i / (double) count;
                ph += 2 * Math.PI * f0 * (1 + (rise - 1) * x) / SAMPLE_RATE;
                double env = Math.min(1, x / 0.1) * Math.exp(-4 * x);
                out[from + i] += amp * Math.sin(ph) * env;
            }
        }

        // Fade out, soft-clip for punch, normalize
        int fade = (int) (0.06 * SAMPLE_RATE);
        double peak = 0;
        for (int i = 0; i < n; i++) {
            if (i >= n - fade) {
                out[i] *= (n - i) / (double) fade;
            }
            out[i] = Math.tanh(1.6 * out[i]);
            peak = Math.max(peak, Math.abs(out[i]));
        }
        for (int i = 0; i < n; i++) {
            out[i] *= 0.92 / peak;
        }
        return out;
    }

    private static void write(double[] samples, File file) throws Exception {
        byte[] pcm = new byte[samples.length * 2];
        for (int i = 0; i < samples.length; i++) {
            short v = (short) Math.round(samples[i] * Short.MAX_VALUE);
            pcm[2 * i] = (byte) v;
            pcm[2 * i + 1] = (byte) (v >> 8);
        }
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        try (AudioInputStream in = new AudioInputStream(new ByteArrayInputStream(pcm), format, samples.length)) {
            AudioSystem.write(in, AudioFileFormat.Type.WAVE, file);
        }
    }
}
