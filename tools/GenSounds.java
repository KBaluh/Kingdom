import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Random;

/**
 * Procedurally synthesizes the underwater rocket sounds (16-bit mono WAV):
 * explosions, launches and a seamless in-flight loop, plus the looping
 * ambient background music (stereo).
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

    // Ambient music. Half the usual sample rate keeps the file small; the
    // music has nothing above a few kHz anyway.
    private static final int MUSIC_RATE = 22050;
    private static final double BAR_SECONDS = 8.0;
    /** Seconds over which one chord cross-fades into the next. */
    private static final double CHORD_FADE = 3.0;
    /** One chord per bar as MIDI notes: bass first, then the pad voicing. */
    private static final int[][] CHORDS = {
        {38, 50, 57, 60, 64, 65},   // Dm9
        {34, 53, 58, 62, 65, 69},   // Bbmaj7
        {41, 53, 57, 60, 64, 67},   // Fmaj9
        {36, 52, 55, 57, 62, 64},   // C6/9
        {43, 55, 58, 62, 65, 69},   // Gm9
        {45, 52, 57, 62, 64, 67},   // A7sus4, leads back to Dm
    };

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

        File musicFile = new File(outDir, "ambient_music.wav");
        double[][] music = music(new Random(0x27D4EB2F165667C5L));
        writeStereo(music[0], music[1], MUSIC_RATE, musicFile);
        System.out.printf("%s  %.2fs%n", musicFile.getPath(), music[0].length / (double) MUSIC_RATE);
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

    /**
     * Slow underwater ambient that loops seamlessly: every layer is written
     * with wrap-around, so whatever rings past the end continues at the start.
     *   - a soft pad of detuned sines, one chord per bar, cross-faded
     *   - a sine bass on the chord root
     *   - sparse bell-like "drops" on chord tones with a ping-pong echo
     *   - a quiet bed of low-passed noise that swells like a current
     * Returns {left, right}.
     */
    private static double[][] music(Random rnd) {
        int n = (int) (CHORDS.length * BAR_SECONDS * MUSIC_RATE);
        double[] left = new double[n];
        double[] right = new double[n];

        for (int c = 0; c < CHORDS.length; c++) {
            int[] chord = CHORDS[c];
            double start = c * BAR_SECONDS - CHORD_FADE / 2;
            double length = BAR_SECONDS + CHORD_FADE;
            addPadTone(left, right, start, length, midiToHz(chord[0]), 0.17, 0.5, 0.4, 0);
            for (int k = 1; k < chord.length; k++) {
                // Three slightly detuned voices spread across the stereo field.
                for (int voice = -1; voice <= 1; voice++) {
                    double freq = midiToHz(chord[k]) * Math.pow(2, voice * 6.5 / 1200);
                    addPadTone(left, right, start, length, freq, 0.045, 0.5 + voice * 0.35, 0.25,
                            rnd.nextDouble() * 2 * Math.PI);
                }
            }
        }

        // Bells go to their own buffers so that only they get the echo.
        double[] bellLeft = new double[n];
        double[] bellRight = new double[n];
        double total = CHORDS.length * BAR_SECONDS;
        double time = 0.75;
        while (time < total) {
            int[] chord = CHORDS[(int) (time / BAR_SECONDS)];
            int note = chord[1 + rnd.nextInt(chord.length - 1)] + (rnd.nextBoolean() ? 12 : 24);
            addBell(bellLeft, bellRight, time, midiToHz(note), 0.05 + rnd.nextDouble() * 0.05, rnd.nextDouble());
            time += 0.75 * (1 + rnd.nextInt(4));
        }
        int echo = (int) (0.5625 * MUSIC_RATE);
        for (int i = 0; i < n; i++) {
            left[i] += bellLeft[i];
            right[i] += bellRight[i];
            double gain = 1;
            for (int tap = 1; tap <= 5; tap++) {
                gain *= 0.5;
                int from = Math.floorMod(i - tap * echo, n);
                // Odd taps cross to the other side: ping-pong.
                left[i] += gain * (tap % 2 == 1 ? bellRight[from] : bellLeft[from]);
                right[i] += gain * (tap % 2 == 1 ? bellLeft[from] : bellRight[from]);
            }
        }

        double[] currentLeft = loopingNoise(n, rnd, 300, MUSIC_RATE);
        double[] currentRight = loopingNoise(n, rnd, 300, MUSIC_RATE);
        for (int i = 0; i < n; i++) {
            // Three and four swells per loop, so both channels wrap cleanly.
            double x = i / (double) n;
            left[i] += 0.5 * currentLeft[i] * (0.6 + 0.4 * Math.sin(2 * Math.PI * 3 * x));
            right[i] += 0.5 * currentRight[i] * (0.6 + 0.4 * Math.sin(2 * Math.PI * 4 * x + 1));
        }

        double peak = 0;
        for (int i = 0; i < n; i++) {
            peak = Math.max(peak, Math.max(Math.abs(left[i]), Math.abs(right[i])));
        }
        for (int i = 0; i < n; i++) {
            left[i] *= 0.8 / peak;
            right[i] *= 0.8 / peak;
        }
        return new double[][] {left, right};
    }

    /**
     * Adds a sustained tone that fades in and out over CHORD_FADE seconds.
     * {@code pan} runs from 0 (left) to 1 (right); {@code octave} is the level
     * of the second harmonic. Indices wrap around the buffer.
     */
    private static void addPadTone(double[] left, double[] right, double start, double length,
            double freq, double amp, double pan, double octave, double phase) {
        int n = left.length;
        int first = (int) Math.round(start * MUSIC_RATE);
        int samples = (int) (length * MUSIC_RATE);
        double gainLeft = amp * Math.cos(pan * Math.PI / 2);
        double gainRight = amp * Math.sin(pan * Math.PI / 2);
        for (int i = 0; i < samples; i++) {
            double t = i / (double) MUSIC_RATE;
            double edge = Math.min(t, length - t) / CHORD_FADE;
            double env = edge >= 1 ? 1 : 0.5 - 0.5 * Math.cos(Math.PI * edge);
            double swell = 1 + 0.2 * Math.sin(2 * Math.PI * 0.13 * t + phase);
            double angle = 2 * Math.PI * freq * t + phase;
            double sample = (Math.sin(angle) + octave * Math.sin(2 * angle)) * env * swell;
            int at = Math.floorMod(first + i, n);
            left[at] += gainLeft * sample;
            right[at] += gainRight * sample;
        }
    }

    /** Adds a soft bell: a sine with a quickly fading octave, ringing for a few seconds. */
    private static void addBell(double[] left, double[] right, double start, double freq, double amp, double pan) {
        int n = left.length;
        int first = (int) Math.round(start * MUSIC_RATE);
        int samples = 4 * MUSIC_RATE;
        double gainLeft = amp * Math.cos(pan * Math.PI / 2);
        double gainRight = amp * Math.sin(pan * Math.PI / 2);
        for (int i = 0; i < samples; i++) {
            double t = i / (double) MUSIC_RATE;
            double angle = 2 * Math.PI * freq * t;
            double env = Math.min(1, t / 0.008) * Math.exp(-t / 0.9);
            double sample = (Math.sin(angle) + 0.3 * Math.exp(-t / 0.4) * Math.sin(2 * angle)) * env;
            int at = Math.floorMod(first + i, n);
            left[at] += gainLeft * sample;
            right[at] += gainRight * sample;
        }
    }

    /** Low-passed noise of length {@code n} whose end runs into its start without a click. */
    private static double[] loopingNoise(int n, Random rnd, double cutoff, int rate) {
        int overlap = rate / 2;
        double[] noise = new double[n + overlap];
        double a = 1 - Math.exp(-2 * Math.PI * cutoff / rate);
        double lp1 = 0, lp2 = 0;
        for (int i = 0; i < noise.length; i++) {
            lp1 += a * (rnd.nextDouble() * 2 - 1 - lp1);
            lp2 += a * (lp1 - lp2);
            noise[i] = lp2;
        }
        double[] out = new double[n];
        for (int i = 0; i < n; i++) {
            if (i < overlap) {
                double x = i / (double) overlap;
                out[i] = noise[i] * Math.sqrt(x) + noise[n + i] * Math.sqrt(1 - x);
            } else {
                out[i] = noise[i];
            }
        }
        return out;
    }

    private static double midiToHz(int note) {
        return 440 * Math.pow(2, (note - 69) / 12.0);
    }

    private static void writeStereo(double[] left, double[] right, int rate, File file) throws Exception {
        byte[] pcm = new byte[left.length * 4];
        for (int i = 0; i < left.length; i++) {
            short l = (short) Math.round(left[i] * Short.MAX_VALUE);
            short r = (short) Math.round(right[i] * Short.MAX_VALUE);
            pcm[4 * i] = (byte) l;
            pcm[4 * i + 1] = (byte) (l >> 8);
            pcm[4 * i + 2] = (byte) r;
            pcm[4 * i + 3] = (byte) (r >> 8);
        }
        AudioFormat format = new AudioFormat(rate, 16, 2, true, false);
        try (AudioInputStream in = new AudioInputStream(new ByteArrayInputStream(pcm), format, left.length)) {
            AudioSystem.write(in, AudioFileFormat.Type.WAVE, file);
        }
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
