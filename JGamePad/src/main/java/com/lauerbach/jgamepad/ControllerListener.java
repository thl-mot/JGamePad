package com.lauerbach.jgamepad;

public interface ControllerListener {
	void valueChanged( byte number, int value);
	void buttonPressed( byte number);
	void buttonReleased( byte number);
}
