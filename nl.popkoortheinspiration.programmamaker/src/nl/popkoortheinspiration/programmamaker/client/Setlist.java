package nl.popkoortheinspiration.programmamaker.client;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

import nl.popkoortheinspiration.programmamaker.shared.Song;

public class Setlist {
	private CellList<SetlistItem> cellList = null;
	private SetlistItemCell itemCell = null;
	private SingleSelectionModel<SetlistItem> selectionModel;
	private ListDataProvider<SetlistItem> dataProvider;

	public Setlist() {
		itemCell = new SetlistItemCell();
		cellList = new CellList<SetlistItem>(itemCell);
		selectionModel = new SingleSelectionModel<SetlistItem>();
		dataProvider = new ListDataProvider<SetlistItem>();
		dataProvider.addDataDisplay(cellList);
		cellList.setSelectionModel(selectionModel);
	    cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

	}

	public void addSong(Song song) {
		SongItem s = new SongItem(song);

		addItemAtPosition(s);
	}

	private void addItemAtPosition(SetlistItem s) {
		SetlistItem selectedItem = selectionModel.getSelectedObject();
		List<SetlistItem> list = dataProvider.getList();
		int idx;

		if (selectedItem == null) {
			idx = -1;
		} else {
			idx = dataProvider.getList().indexOf(selectedItem);
		}

		if (idx < 0 || idx + 1 >= list.size()) {
			list.add(s);
			selectionModel.setSelected(s, true);
		} else {
			idx++;
			list.add(idx, s);
			selectionModel.setSelected(s, true);
		}
	}

	public void addBreak() {
		Break b = new Break();

		addItemAtPosition(b);
	}

	public Song removeSelectedItem() {
		SetlistItem selectedItem = selectionModel.getSelectedObject();
		if (selectedItem != null) {
			List<SetlistItem> list = dataProvider.getList();
			int idx = dataProvider.getList().indexOf(selectedItem);
			list.remove(selectedItem);
			selectionModel.setSelected(list.get(idx), true);
		}
		if (selectedItem instanceof SongItem) {
			return ((SongItem) selectedItem).getSong();
		}
		else {
			return null;
		}
	}

	public void moveUp() {
		SetlistItem selectedItem = selectionModel.getSelectedObject();
		if (selectedItem != null) {
			List<SetlistItem> list = dataProvider.getList();
			int idx = list.indexOf(selectedItem);

			if (idx > 0) {
				list.remove(idx);
				list.add(idx - 1, selectedItem);
			}
		}
	}

	public void moveDown() {
		SetlistItem selectedItem = selectionModel.getSelectedObject();
		if (selectedItem != null) {
			List<SetlistItem> list = dataProvider.getList();
			int idx = list.indexOf(selectedItem);

			if (idx < list.size() - 1) {

				list.remove(idx);
				list.add(idx + 1, selectedItem);

			}
		}
	}

	public String getDurationAsString() {
		int duration = 0;
		duration = dataProvider.getList().stream().filter(i -> i instanceof SongItem).map(i -> ((SongItem) i).getLength()).reduce(0,
				(s, e) -> s + e);
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

	class SetlistItem {
		public String getRepr() {
			return "<null>";
		}
	}

	class Break extends SetlistItem {
		public Break() {
		}

		@Override
		public String getRepr() {
			List<SetlistItem> list = dataProvider.getList();
			int idx = list.indexOf(this) - 1;
			int sectionLength = 0;

			while (idx >= 0) {
				SetlistItem item = list.get(idx);
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

	public class SongItem extends SetlistItem {
		private String repr;
		private Song song;

		public SongItem(Song s) {
			song = s;
		}

		public int getLength() {
			return song.getLength();
		}

		public Song getSong() {
			return song;
		}

		@Override
		public String getRepr() {
			repr = song.getTitle();
			repr += " (" + getDurationAsString(song.getLength()) + ")";
			return repr;
		}
	}

	static class SetlistItemCell extends AbstractCell<SetlistItem> {
		public SetlistItemCell() {
			super("click", "dblclick");
		}

		interface Templates extends SafeHtmlTemplates {
			@SafeHtmlTemplates.Template("<div style=\"{0}\">{1}</div>")
			SafeHtml cell(SafeStyles styles, SafeHtml value);
		}
		private static Templates templates = GWT.create(Templates.class);

		@Override
		public void render(Context context, SetlistItem value, SafeHtmlBuilder sb) {
			if (value == null)
				return;
			// If the value comes from the user, we escape it to avoid XSS attacks.
			SafeHtml safeValue = SafeHtmlUtils.fromString(value.getRepr());

			// Use the template to create the Cell's html.
			SafeStyles styles = SafeStylesUtils.forTrustedColor(getColorFromItem(value));
			SafeHtml rendered = templates.cell(styles, safeValue);
			sb.append(rendered);
		}
		
		private String getColorFromItem(SetlistItem value) {
			if (value instanceof SongItem) {
				return "#ffffff";
			}
			return "#a0a0a0";
		}
	}

	public Widget getWidget() {
		return cellList;
	}
}