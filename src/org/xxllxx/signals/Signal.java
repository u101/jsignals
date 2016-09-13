package org.xxllxx.signals;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xx][xx
 */
public class Signal {

	private int id = -1;
	private String name = null;

	private final List<Slot> slots = new ArrayList<Slot>(4);
	private final IntArray slotsToRemove = new IntArray();
	private int isIterating = 0;

	public Signal() {}

	public Signal(int id) {
		this.id = id;
	}

	public Signal(String name) {
		this.name = name;
	}

	// -----------------------------------------------------------------------------------------------------------------
	//					SIGNAL ID
	// -----------------------------------------------------------------------------------------------------------------

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// -----------------------------------------------------------------------------------------------------------------
	//					ADD : REMOVE : DISPATCH
	// -----------------------------------------------------------------------------------------------------------------

	public Slot add(SignalListener listener) {
		return add(listener,false);
	}

	public Slot addOnce(SignalListener listener) {
		return add(listener,true);
	}

	public Slot add(SignalListener listener, boolean once) {
		final int index = indexOfSlot(listener);
		if (index == -1) {
			// we can get slot from pool
			Slot slot = newSlot();
			slot.listener = listener;
			slot.once = once;
			slots.add(slot);
			return slot;
		} else {
			return slots.get(index);
		}
	}

	public void remove(SignalListener listener) {
		int i = indexOfSlot(listener);
		if (i == -1) return;

		Slot slot = slots.get(i);
		slot.listener = null;

		if (isIterating > 0) {
			slotsToRemove.add(i);
		} else {
			slots.remove(i);
			releaseSlot(slot);
		}
	}


	public void dispatch(Object ...args) {

		Slot slot;

		SignalData data = newSignalData();
		data.signal = this;
		data.signalArgs = args;

		++isIterating;

		// dispatch
		int i = 0;
		int ln = slots.size();
		while (i < ln) {
			slot = slots.get(i);
			if (slot.listener != null) {

				data.slotArgs = slot.params;
				slot.listener.onSignal(data);

				// remove once signal
				if (slot.listener != null && slot.once) {
					slot.listener = null;
					slotsToRemove.add(i);
				}

			}
			++i;
		}

		--isIterating;

		releaseSignalData(data);

		if (isIterating == 0) {
			int j = slotsToRemove.size;
			while (j > 0) {
				--j;
				int slotIndex = slotsToRemove.items[j];
				slot = slots.remove(slotIndex);
				releaseSlot(slot);
			}
			slotsToRemove.size = 0;
		}
	}

	private int indexOfSlot(SignalListener listener) {
		int i = slots.size() - 1;
		while (i >= 0) {
			Slot slot = slots.get(i);
			if (slot.listener == listener) return i;
			--i;
		}
		return -1;
	}

	// -----------------------------------------------------------------------------------------------------------------
	//					SLOT
	// -----------------------------------------------------------------------------------------------------------------

	public static class Slot {
		SignalListener listener = null;
		Object[] params = null;
		boolean once = false;

		public void setParams(Object ...params) {
			this.params = params;
		}
	}

	// -----------------------------------------------------------------------------------------------------------------
	//					UTILS
	// -----------------------------------------------------------------------------------------------------------------

	static final Pool<Slot> SlotsPool = new Pool<Slot>(new Slot[256]);
	static final Pool<SignalData> DataPool = new Pool<SignalData>(new SignalData[32]);

	SignalData newSignalData() {
		if (DataPool.size == 0) {
			return new SignalData();
		} else {
			return DataPool.remove();
		}
	}

	void releaseSignalData(SignalData data) {
		data.clear();
		DataPool.add(data);
	}

	Slot newSlot() {
		if (SlotsPool.size == 0) {
			return new Slot();
		} else {
			return SlotsPool.remove();
		}
	}

	void releaseSlot(Slot slot) {
		slot.listener = null;
		slot.params = null;
		SlotsPool.add(slot);
	}

	static class Pool<T> {

		final T[] items;
		int size = 0;

		Pool(T[] items) {
			this.items = items;
		}

		void add(T value) {
			if (size < items.length) {
				items[size] = value;
				++size;
			}
		}

		T remove() {
			--size;
			T value = items[size];
			items[size] = null;
			return value;
		}
	}

	static class IntArray {

		int[] items = new int[16];
		int size = 0;

		void add (int value) {
			int[] items = this.items;
			if (size == items.length) {
				items = resize(Math.max(8, (int)(size * 1.75f)));
			}
			items[size++] = value;
		}

		private int[] resize (int newSize) {
			int[] newItems = new int[newSize];
			int[] items = this.items;
			System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
			this.items = newItems;
			return newItems;
		}
	}


}
