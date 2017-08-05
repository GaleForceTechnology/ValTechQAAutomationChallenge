package technology.galeforce.testframework.screenrecorder;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.io.File;
import java.io.IOException;

import technology.galeforce.testframework.TestFramework;
import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.screenrecorder.ScreenRecorder;

/**
 * Created by peter.gale on 30/01/2017.
 */

public class extendedScreenRecorder extends ScreenRecorder {

    // Original:
    //public SpecializedScreenRecorder(GraphicsConfiguration cfg,
    //        Rectangle captureArea, Format fileFormat, Format screenFormat,
    //        Format mouseFormat, Format audioFormat, File movieFolder,
    //        String name) throws IOException, AWTException {
    // setting capture area doesn't seem to be necessary now
    // We set the file name here in this class!
    public extendedScreenRecorder(
            GraphicsConfiguration cfg,
            Format fileFormat,
            Format screenFormat,
            Format mouseFormat,
            Format audioFormat,
            File movieFolder) throws IOException, AWTException {
        // Original:     super(cfg, captureArea, fileFormat, screenFormat, mouseFormat,audioFormat, movieFolder);
        super(cfg, fileFormat, screenFormat, mouseFormat, audioFormat);
        this.movieFolder=movieFolder;
    }

    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {
        if (!movieFolder.exists()) {
            movieFolder.mkdirs();
        } else if (!movieFolder.isDirectory()) {
            throw new IOException("\"" + movieFolder + "\" is not a directory.");
        }

        String screenRecordingFileName = "ScreenRecording-" + TestFramework.getAGuid() + "." + Registry.getInstance().getExtension(fileFormat);
        // In windows, the filename cannot contain colon ":" characters, as supplied by the guid function, so replace these for something more file friendly
        screenRecordingFileName = screenRecordingFileName.replace(":","-");
        return new File(movieFolder, screenRecordingFileName);

    }

}