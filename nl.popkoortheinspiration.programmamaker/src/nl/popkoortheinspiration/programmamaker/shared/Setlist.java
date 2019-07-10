package nl.popkoortheinspiration.programmamaker.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ListBox;

public class Setlist {
	private List<ISetlistItem> items = new ArrayList<ISetlistItem>();
	private ListBox list;

	public Setlist(ListBox l) {
		list = l;
		list.clear();
	}

	public void addSong(Song song) {
		SongItem s = new SongItem(song);

		addItemAtPosition(s);
	}

	private void addItemAtPosition(ISetlistItem s) {
		int idx = list.getSelectedIndex();
		if (idx < 0 || idx+1>=list.getItemCount()) {
			items.add(s);
			list.addItem(s.getRepr());
			list.setSelectedIndex(list.getItemCount()-1);
		} else {
			idx++;
			items.add(idx, s);
			list.insertItem(s.getRepr(), idx);
			list.setSelectedIndex(idx);
		}
	}

	public void addBreak() {
		Break b = new Break();
		
		addItemAtPosition(b);
	}

	public void remove(int idx) {
		if ((idx >= 0) && (idx < items.size())) {
			list.removeItem(idx);
			items.remove(idx);
		}
		// Only the next break can be affected
		// Find it and update it.
		while (idx < items.size()) {
			ISetlistItem item = items.get(idx);
			if (item instanceof Break) {
				String repr = item.getRepr();
				list.setItemText(idx, repr);
				break;
			}
			idx++;
		}
	}

	public void moveUp(int idx) {
		if (idx > 0) {
			ISetlistItem item = items.get(idx);
			items.remove(idx);
			items.add(idx - 1, item);
			list.removeItem(idx);
			list.insertItem(item.getRepr(), idx - 1);
			list.setSelectedIndex(idx - 1);

			// At most two breaks can be affected, the one that follows
			// the current location of the item and the subsequent one.
			// The latter is the case when a song is moved from one
			// section to a prior one.
			while (idx < items.size()) {
				item = items.get(idx);
				if (item instanceof Break) {
					String repr = item.getRepr();
					list.setItemText(idx, repr);
					break;
				}
				idx++;
			}

			idx++;
			while (idx < items.size()) {
				item = items.get(idx);
				if (item instanceof Break) {
					String repr = item.getRepr();
					list.setItemText(idx, repr);
					break;
				}
				idx++;
			}
		}
	}

	public void moveDown(int idx) {
		if (idx < items.size() - 1) {
			ISetlistItem item = items.get(idx);

			items.remove(idx);
			items.add(idx + 1, item);
			list.removeItem(idx);
			list.insertItem(item.getRepr(), idx + 1);
			list.setSelectedIndex(idx + 1);

			// Adjust break duration of preceding section
			int pidx = idx;
			while (pidx >= 0) {
				item = items.get(pidx);
				if (item instanceof Break) {
					String repr = item.getRepr();
					list.setItemText(pidx, repr);
					break;
				}
				pidx--;
			}

			// Adjust break duration of next section
			pidx = idx;
			while (pidx < items.size()) {
				item = items.get(pidx);
				if (item instanceof Break) {
					String repr = item.getRepr();
					list.setItemText(pidx, repr);
					break;
				}
				pidx++;
			}
		}
	}

	public String getDurationAsString() {
		int duration = 0;
		duration = items.stream()
				.filter(i -> i instanceof SongItem)
				.map(i -> ((SongItem)i).getLength())
				.reduce(0, (s,e)-> s+e);
		return getDurationAsString(duration);
	}
	
	private String getDurationAsString(int length) {
		int minutes = length / 60;
		int seconds = length % 60;

		String result = minutes + ":";
		if (seconds < 10)
			result += "0";
		result += seconds;

		return result;
	}

	interface ISetlistItem {
		public String getRepr();
	}

	class Break implements ISetlistItem {
		public Break() {
		}

		@Override
		public String getRepr() {
			int idx = items.indexOf(this) - 1;
			int sectionLength = 0;

			while (idx >= 0) {
				ISetlistItem item = items.get(idx);
				if (item instanceof Break)
					break;
				if (item instanceof SongItem) {
					sectionLength += ((SongItem) item).getLength();
				}
				idx--;
			}

			String sectionDuration = getDurationAsString(sectionLength);
			return (" --- Pauze (" + sectionDuration + ") ---");
		}
	}

	public class SongItem implements ISetlistItem {
		private String repr;
		private Song song;

		public SongItem(Song s) {
			song = s;
			setRepr();
		}

		private void setRepr() {
			repr = song.getTitle();
			repr += " (" + getDurationAsString(song.getLength()) + ")";
		}

		public int getLength() {
			return song.getLength();
		}

		@Override
		public String getRepr() {
			return repr;
		}
	}
}
