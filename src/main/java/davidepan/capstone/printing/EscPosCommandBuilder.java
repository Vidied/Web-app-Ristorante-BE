package davidepan.capstone.printing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class EscPosCommandBuilder {
    private static final Charset PRINTER_CHARSET = Charset.forName("CP437");
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private int lineCount = 0;

    public EscPosCommandBuilder init() {
        write(0x1B, 0x40);
        return this;
    }

    public EscPosCommandBuilder text(String text) {
        try {
            buffer.write(text.getBytes(PRINTER_CHARSET));
        } catch (IOException ignored) {
        }
        return this;
    }

    public EscPosCommandBuilder alignLeft() { write(0x1B, 0x61, 0x00); return this; }
    public EscPosCommandBuilder alignCenter() { write(0x1B, 0x61, 0x01); return this; }

    public EscPosCommandBuilder bold(boolean on) { write(0x1B, 0x45, on ? 1 : 0); return this; }
    public EscPosCommandBuilder doubleHeight(boolean on) { write(0x1B, 0x21, on ? 0x10 : 0x00); return this; }

    public EscPosCommandBuilder feed(int lines) {
        for (int i = 0; i < lines; i++) write(0x0A);
        return this;
    }

    public EscPosCommandBuilder cut() {
        write(0x1D, 0x56, 0x00);
        return this;
    }

    public EscPosCommandBuilder newLine() {
        write(0x0A);
        lineCount++;
        return this;
    }

    public EscPosCommandBuilder characterSize(int widthMultiplier, int heightMultiplier) {
        int w = Math.clamp(widthMultiplier, 1, 8) - 1;
        int h = Math.clamp(heightMultiplier, 1, 8) - 1;
        write(0x1D, 0x21, (w << 4) | h);
        return this;
    }

    public EscPosCommandBuilder resetCharacterSize() {
        write(0x1D, 0x21, 0x00);
        return this;
    }

    public EscPosCommandBuilder padToMinimumLines(int minLines) {
        while (lineCount < minLines) {
            newLine();
        }
        return this;
    }

    public EscPosCommandBuilder beep(int times, int duration) {
        int safeTimes = Math.clamp(times, 1, 9);
        int safeDuration = Math.clamp(duration, 1, 9);

        write(0x1B, 0x42, safeTimes, safeDuration);
        return this;
    }

    public EscPosCommandBuilder beep() {
        return beep(2, 2);
    }

    private void write(int... bytes) {
        for (int b : bytes) buffer.write(b);
    }

    public byte[] build() {
        return buffer.toByteArray();
    }
}