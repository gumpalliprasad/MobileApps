package myschoolapp.com.gsnedutech.Util;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;

public class AudioRecorder {

    private MediaRecorder recorder = new MediaRecorder();

    Context ctx;

    private File outfile = null;

    public AudioRecorder(Context ctx){
        this.ctx = ctx;
    }

    public void startRecording(String audioFile) throws IOException {
        String state = android.os.Environment.getExternalStorageState();
        if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
            throw new IOException("SD Card is not mounted.  It is " + state + ".");
        }

        File directory = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            directory = new File(ctx.getExternalFilesDir(null) + "/audio");
        }else {
             directory = new File(Environment.getExternalStorageDirectory(), "audio");
        }


                // make sure the directory we plan to store the recording in exists

        if (!directory.exists()) {
            directory.mkdirs();
        }
        try{
            File storageDir = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                storageDir = new File(ctx.getExternalFilesDir(null) + "/audio/");
            }else {
                storageDir = new File(Environment.getExternalStorageDirectory(), "audio/");
            }
//            File storageDir = new File(Environment
//                    .getExternalStorageDirectory(), "/audio/");
            storageDir.mkdir();
            outfile=File.createTempFile(audioFile, ".wav",storageDir);
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(outfile.getAbsolutePath());
        }catch(IOException e){
            e.printStackTrace();
        }

        try{
            recorder.prepare();
        }catch(IllegalStateException e){
            e.printStackTrace();
        }

        recorder.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pause()throws IOException{
        recorder.pause();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void resume(){
        recorder.resume();
    }

    public void stop() throws IOException {
        recorder.stop();
        recorder.release();
    }

    public void renameFile(String name){

        File f = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            f = new File(ctx.getExternalFilesDir(null) + "/audio/"+name+".wav");
        }else {
            f = new File(Environment.getExternalStorageDirectory(), "/audio/"+name+".wav");
        }

//        File f = new File(Environment
//                .getExternalStorageDirectory(), "/audio/"+name+".wav");
        boolean success = outfile.renameTo(f);

        if (success){
            Log.v("tag","success");
        }else {

        }
    }

    public File getOutfile() {
        return outfile;
    }

    public void deleteFile(){
        outfile.delete();
    }
}
