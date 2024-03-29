package library.addons.audio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.BufferUtils;

import library.EngineTools;

public class WaveData {

	final int samplerate;
	final int totalBytes;
	final int bytesPerFrame;
	final ByteBuffer data;

	private final AudioInputStream audioStream;
	private final byte[] dataArray;

	private WaveData(AudioInputStream stream) {
		this.audioStream = stream;
		AudioFormat audioFormat = stream.getFormat();
		System.out.println(audioFormat.getChannels());
		System.out.println(audioFormat.getSampleSizeInBits());
		//format = getOpenAlFormat(audioFormat.getChannels(), audioFormat.getSampleSizeInBits());
		this.samplerate = (int) audioFormat.getSampleRate();
		this.bytesPerFrame = audioFormat.getFrameSize();
		this.totalBytes = (int) (stream.getFrameLength() * bytesPerFrame);
		this.data = BufferUtils.createByteBuffer(totalBytes);
		this.dataArray = new byte[totalBytes];
		loadData();
	}

	protected void dispose() {
		try {
			audioStream.close();
			data.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private ByteBuffer loadData() {
		try {
			int bytesRead = audioStream.read(dataArray, 0, totalBytes);
			data.clear();
			data.put(dataArray, 0, bytesRead);
			data.flip();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Couldn't read bytes from audio stream!");
		}
		return data;
	}


	public static WaveData create(String file){
		InputStream stream = EngineTools.mClass.getResourceAsStream("/assets/audio/"+file);
		if(stream==null){
			System.err.println("Couldn't find file: " + file);
			return null;
		}
		InputStream bufferedInput = new BufferedInputStream(stream);
		AudioInputStream audioStream = null;
		try {
			audioStream = AudioSystem.getAudioInputStream(bufferedInput);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		WaveData wavStream = new WaveData(audioStream);
		return wavStream;
	}
}