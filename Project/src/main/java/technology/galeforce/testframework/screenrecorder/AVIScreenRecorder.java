package technology.galeforce.testframework.screenrecorder;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.File;

import org.monte.screenrecorder.ScreenRecorder;
import org.monte.media.Format;
import org.monte.media.math.Rational;

import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

/**
 * Created by peter.gale on 30/01/2017.
 */

public class AVIScreenRecorder {

    private static ScreenRecorder screenRecorder;

    static Format fileFormat=new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI);

    static long recordingStartTime;
    static long currentTime;
    static long elapsedTime;

    public static void  startScreenCast(String rootDirectoryForRecordedVideo) throws Exception {
        // Create a instance of GraphicsConfiguration to get the Graphics configuration
        // of the Screen. This is needed for ScreenRecorder class.
        GraphicsConfiguration gc = GraphicsEnvironment//
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice()
            .getDefaultConfiguration();

        File movieFolder = new File(rootDirectoryForRecordedVideo);

        //Create a instance of ScreenRecorder with the required configurations
        screenRecorder = new extendedScreenRecorder(
            gc,
            fileFormat,
            new Format(
                MediaTypeKey, MediaType.VIDEO,
                EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                DepthKey, (int)24,
                FrameRateKey, Rational.valueOf(15),
                QualityKey, 1.0f,
                KeyFrameIntervalKey, (int) (15 * 60)),
            new Format(
                MediaTypeKey, MediaType.VIDEO,
                EncodingKey, "no",
                FrameRateKey, Rational.valueOf(30)),
            null,
            movieFolder);
        // For a cursor, use, e.g. "black" instead of "no" (No cursor)

        // Call the start method of ScreenRecorder to begin recording
        screenRecorder.start();

    }

    public static void  stopScreenCast() {
        // Ignore any errors - we may not have started the screen recorder!?
        try { screenRecorder.stop(); } catch (Exception e) {}
    }

}