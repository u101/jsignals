package org.xxllxx.signals;

/**
 * Promise is a once dispatched signal (cannot be called twice)
 * Listeners added after signal dispatched are called automatically
 * @author xx][xx
 */
public class Promise extends Signal {

	private boolean isDispatched = false;
	private Object[] args = null;

	public Promise() {}

	public Promise(int id) {
		super(id);
	}

	public Promise(String name) {
		super(name);
	}

	@Override
	public void dispatch(Object... args) {
		if (isDispatched) {
			throw new IllegalStateException("You cannot dispatch() a Promise more than once");
		}
		isDispatched = true;
		this.args = args;
		super.dispatch(args);
	}

	/**
	 * Returns null !!
	 */
	@Override
	public Slot add(SignalListener listener, boolean once) {
		if (isDispatched) {
			SignalData data = newSignalData();
			data.signal = this;
			data.signalArgs = args;
			listener.onSignal(data);
			releaseSignalData(data);
		} else {
			super.add(listener, true);
		}
		return null;
	}
}
