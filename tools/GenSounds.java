import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Random;

/**
 * Procedurally synthesizes the underwater rocket sounds (16-bit mono WAV):
 * explosions, launches and a seamless in-flight loop.
 *
 * Run from the project root:
 *   java tools/GenSounds.java [outputDir]
 *
 * Each explosion is a mix of:
 *   - a short high-passed noise "crack" (the detonation transient)
 *   - a sine "boom" sweeping down from ~130 Hz to ~35 Hz
 *   - a noise body whose low-pass cutoff closes over time (muffled by water)
 *   - a slow low rumble tail
 *   - a handful of rising sine chirps (bubbles)
 */
public class GenSounds {

    private static final int SAMPLE_RATE = 44100;
    private static final int EXPLOSION_VARIANTS = 4;
    private static final int LAUNCH_VARIANTS = 3;

    public static void main(String[] args) throws Exception {
        File outDir = new File(args.length > 0 ? args[0] : "godot/assets/sounds");
        if (!outDir.isDirectory() && !outDir.mkdirs()) {
            throw new IllegalStateException("Cannot create " + outDir);
        }
        for (int i = 1; i <= EXPLOSION_VARIANTS; i++) {
            save(explosion(new Random(i * 0x9E3779B97F4A7C15L)), new File(outDir, "rocket_explosion" + i + ".wav"));
        }
        for (int i = 1; i <= LAUNCH_VARIANTS; i++) {
            save(launch(new Random(i * 0xC2B2AE3D27D4EB4FL)), new File(outDir, "rocket_launch" + i + ".wav"));
        }
        save(flightLoop(new Random(0x165667B19E3779F9L)), new File(outDir, "rocket_flight.wav"));
    }

    private static void save(double[] samples, File file) throws Exception {
        write(samples, file);
        System.out.printf("%s  %.2fs%n", file.getPath(), samples.length / (double) SAMPLE_RATE);
    }

    /**
     * Rocket leaving the tube: a short low thump, a burst of compressed air
     * that opens up and closes again, and a few bubbles.
     */
    private static double[] launch(Random rnd) {
        double duration = 0.42 + rnd.nextDouble() * 0.08;
        int n = (int) (duration * SAMPLE_RATE);
        double[] out = new double[n];

        double fStart = 170 + rnd.nextDouble() * 40;
        double fEnd = 60 + rnd.nextDouble() * 15;
        double phase = 0;
        for (int i = 0; i < n; i++) {
            double t = i / (double) SAMPLE_RATE;
            double f = fEnd + (fStart - fEnd) * Math.exp(-28 * t);
            phase += 2 * Math.PI * f / SAMPLE_RATE;
            out[i] += 0.8 * Math.sin(phase) * Math.exp(-16 * t) * Math.min(1, t / 0.002);
        }

        double peakAt = 0.04 + rnd.nextDouble() * 0.03;
        double burstDecay = 8 + rnd.nextDouble() * 3;
        double lp1 = 0, lp2 = 0, low = 0;
        double lowA = 1 - Math.exp(-2 * Math.PI * 140.0 / SAMPLE_RATE);
        for (int i = 0; i < n; i++) {
            double t = i / (double) SAMPLE_RATE;
            double white = rnd.nextDouble() * 2 - 1;
            double bump = (t - peakAt) / 0.07;
            double cutoff = 350 + 2600 * Math.exp(-bump * bump);
            double a = 1 - Math.exp(-2 * Math.PI * cutoff / SAMPLE_RATE);
            lp1 += a * (white - lp1);
            lp2 += a * (lp1 - lp2);
            low += lowA * (lp2 - low);
            double env = Math.min(1, t / 0.004) * Math.exp(-burstDecay * t);
            out[i] += 1.4 * (lp2 - low) * env;
        }

        addBubbles(out, rnd, 4 + rnd.nextInt(4), 0.05, duration - 0.15, 0.10);
        return finish(out, 0.03, 1.4, 0.85);
    }

    /**
     * One second of rocket motor under water that loops without a click:
     * band-passed noise whose tail is cross-faded into its head, shaped by a
     * burble and a low hum that both complete whole cycles within the loop.
     */
    private static double[] flightLoop(Random rnd) {
        int n = SAMPLE_RATE;
        int overlap = SAMPLE_RATE / 5;
        double[] noise = new double[n + overlap];
        double lp1 = 0, lp2 = 0, low = 0;
        // A low cutoff keeps it a muffled rumble rather than a hiss.
        double a = 1 - Math.exp(-2 * Math.PI * 420.0 / SAMPLE_RATE);
        double lowA = 1 - Math.exp(-2 * Math.PI * 90.0 / SAMPLE_RATE);
        for (int i = 0; i < noise.length; i++) {
            double white = rnd.nextDouble() * 2 - 1;
            lp1 += a * (white - lp1);
            lp2 += a * (lp1 - lp2);
            low += lowA * (lp2 - low);
            noise[i] = lp2 - low;
        }

        double[] out = new double[n];
        for (int i = 0; i < n; i++) {
            double v = noise[i];
            if (i < overlap) {
                double x = i / (double) overlap;
                v = noise[i] * Math.sqrt(x) + noise[n + i] * Math.sqrt(1 - x);
            }
            double t = i / (double) SAMPLE_RATE;
            double burble = 0.6 + 0.25 * Math.sin(2 * Math.PI * 23 * t) + 0.15 * Math.sin(2 * Math.PI * 37 * t + 1);
            double hum = 0.06 * Math.sin(2 * Math.PI * 64 * t) + 0.03 * Math.sin(2 * Math.PI * 128 * t);
            out[i] = v * burble + hum;
        }
        return finish(out, 0, 1.0, 0.7);
    }

    /** Short rising sine chirps scattered between {@code from} and {@code to} seconds. */
    private static void addBubbles(double[] out, Random rnd, int count, double from, double to, double maxAmp) {
        for (int b = 0; b < count; b++) {
            double start = from + rnd.nextDouble() * (to - from);
            double len = 0.03 + rnd.nextDouble() * 0.045;
            double f0 = 380 + rnd.nextDouble() * 1000;
            double rise = 1.3 + rnd.nextDouble() * 0.6;
            double amp = maxAmp * (0.4 + 0.6 * rnd.nextDouble());
            int first = (int) (start * SAMPLE_RATE);
            int samples = (int) (len * SAMPLE_RATE);
            double ph = 0;
            for (int i = 0; i < samples && first + i < out.length; i++) {
                double x = i / (double) samples;
                ph += 2 * Math.PI * f0 * (1 + (rise - 1) * x) / SAMPLE_RATE;
                out[first + i] += amp * Math.sin(ph) * Math.min(1, x / 0.1) * Math.exp(-4 * x);
            }
        }
    }

    /** Fades the tail out, soft-clips with the given drive and normalizes to {@code peakLevel}. */
    private static double[] finish(double[] out, double fadeSeconds, double drive, double peakLevel) {
        int n = out.length;
        int fade = (int) (fadeSeconds * SAMPLE_RATE);
        double peak = 0;
        for (int i = 0; i < n; i++) {
            if (i >= n - fade) {
                out[i] *= (n - i) / (double) fade;
            }
            out[i] = Math.tanh(drive * out[i]);
            peak = Math.max(peak, Math.abs(out[i]));
        }
        for (int i = 0; i < n; i++) {
            out[i] *= peakLevel / peak;
        }
        return out;
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
