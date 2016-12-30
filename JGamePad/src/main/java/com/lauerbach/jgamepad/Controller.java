package com.lauerbach.jgamepad;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Controller implements Runnable, ControllerListener {
	private DataInputStream in;
	private Thread thread;

	public static final byte VALUE_LT = 2;
	public static final byte VALUE_RT = 5;

	public static final byte VALUE_1_LEFT_RIGHT = 6;
	public static final byte VALUE_1_UP_DOWN = 7;

	public static final byte VALUE_2_LEFT_RIGHT = 0;
	public static final byte VALUE_2_UP_DOWN = 1;

	public static final byte VALUE_3_LEFT_RIGHT = 3;
	public static final byte VALUE_3_UP_DOWN = 4;

	public static final byte BUTTON_A = 0;
	public static final byte BUTTON_B = 1;
	public static final byte BUTTON_X = 2;
	public static final byte BUTTON_Y = 3;
	public static final byte BUTTON_LB = 4;
	public static final byte BUTTON_RB = 5;
	public static final byte BUTTON_BACK = 6;
	public static final byte BUTTON_START = 7;

	int lt = 0, rt = 0, leftRight1 = 0, upDown1 = 0, leftRight2 = 0, upDown2 = 0, leftRight3 = 0, upDown3 = 0;

	List<ControllerListener> listeners = new ArrayList<ControllerListener>();

	public Controller(int jsNumber) throws FileNotFoundException {
		in = new DataInputStream(new BufferedInputStream(new FileInputStream("/dev/input/js" + jsNumber)));
		thread = new Thread(this);
		thread.start();
	}

	public void close() {
		try {
			in.close();
			in = null;
		} catch (IOException e) {
		}
	}

	public void run() {
		byte packet[] = new byte[8];
		try {
			while (in != null && in.read(packet) != -1) {
				long time = (int) (packet[0] & 0xFF) + (int) (packet[1] & 0xFF) * 0x00000100
						+ (int) (packet[2] & 0xFF) * 0x00010000 + (int) (packet[3] & 0x01000000);
				int value = (int) (packet[4] & 0xFF) + (int) (packet[5]) * 0x0100;
				byte type = packet[6];
				byte number = packet[7];
				received(time, value, type, number);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void received(long time, int value, byte type, byte number) {
		if (type == 1) {
			// Buttons
			if (value == 1) {
				buttonPressed(number);
			} else {
				buttonReleased(number);
			}
		} else if (type == 2) {
			valueChanged(number, value);
		}
	}

	public void valueChanged(byte number, int value) {
		switch (number) {
		case VALUE_RT:
			rt = value;
			break;
		case VALUE_LT:
			lt = value;
			break;
		case VALUE_1_LEFT_RIGHT:
			leftRight1 = value;
			break;
		case VALUE_1_UP_DOWN:
			upDown1 = value;
			break;
		case VALUE_2_LEFT_RIGHT:
			leftRight2 = value;
			break;
		case VALUE_2_UP_DOWN:
			upDown2 = value;
			break;
		case VALUE_3_LEFT_RIGHT:
			leftRight3 = value;
			break;
		case VALUE_3_UP_DOWN:
			upDown3 = value;
			break;
		}
		Iterator<ControllerListener> i = listeners.iterator();
		while (i.hasNext()) {
			i.next().valueChanged(number, value);
		}
	}

	public void buttonPressed(byte number) {
		Iterator<ControllerListener> i = listeners.iterator();
		while (i.hasNext()) {
			i.next().buttonPressed(number);
		}
	}

	public void buttonReleased(byte number) {
		Iterator<ControllerListener> i = listeners.iterator();
		while (i.hasNext()) {
			i.next().buttonReleased(number);
		}
	}

	public void addListener( ControllerListener listener) {
		listeners.add( listener);
	}
	
	public void removeListener( ControllerListener listener) {
		listeners.remove( listener);
	}

	public int getUpDown3() {
		return upDown3;
	}

	public int getLeftRight3() {
		return leftRight3;
	}

	public int getUpDown1() {
		return upDown1;
	}
}
