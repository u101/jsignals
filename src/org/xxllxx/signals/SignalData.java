package org.xxllxx.signals;

/**
 * @author xx][xx
 */
public class SignalData {

	static final Object[] emptyArgs = new Object[0];

	Signal signal = null;

	public Signal getSignal() {
		return signal;
	}

	// -----------------------------------------------------------------------------------------------------------------
	//					SIGNAL ARGUMENTS
	// -----------------------------------------------------------------------------------------------------------------

	Object[] signalArgs = emptyArgs;

	public Object getArg() { return getArg(0); }

	public Object getArg(int index) {
		return index >= 0 && index < signalArgs.length ? signalArgs[index] : null;
	}

	public <T> T getTypedArg(Class<T> clazz) {
		return getTypedArg(0, clazz);
	}

	public <T> T getTypedArg(int index, Class<T> clazz) {
		return castArg(getArg(index), clazz);
	}

	// -----------------------------------------------------------------------------------------------------------------
	//					SLOT ARGUMENTS
	// -----------------------------------------------------------------------------------------------------------------

	Object[] slotArgs = emptyArgs;

	public Object getSlotArg() { return getSlotArg(0); }

	public Object getSlotArg(int index) {
		return slotArgs != null && index >= 0 && index < slotArgs.length ?
				slotArgs[index] : null;
	}

	public <T> T getTypedSlotArg(Class<T> clazz) {
		return getTypedSlotArg(0, clazz);
	}

	public <T> T getTypedSlotArg(int index, Class<T> clazz) {
		return castArg(getSlotArg(index), clazz);
	}

	// -----------------------------------------------------------------------------------------------------------------
	//					INTERNAL
	// -----------------------------------------------------------------------------------------------------------------

	private <T> T castArg(Object arg, Class<T> clazz) {
		if (arg == null) return null;
		if (clazz == null) {
			throw new IllegalArgumentException("clazz argument is null");
		}
		@SuppressWarnings("unchecked")
		final T result = (T) arg;
		return result;
	}

	void clear() {
		signal = null;
		signalArgs = null;
		slotArgs = null;
	}


}
