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

public class RepertoireList {
	private CellList<RepertoireSong> cellList = null;
	private SongCell songCell = null;
	private SingleSelectionModel<RepertoireSong> selectionModel;
	private ListDataProvider<RepertoireSong> dataProvider;

	public RepertoireList() {
		songCell = new SongCell();
		cellList = new CellList<RepertoireSong>(songCell);
		selectionModel = new SingleSelectionModel<RepertoireSong>();
		dataProvider = new ListDataProvider<RepertoireList.RepertoireSong>();
		dataProvider.addDataDisplay(cellList);
		cellList.setSelectionModel(selectionModel);
	    cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
	}

	public void initialize(List<Song> repList) {
		List<RepertoireSong> list = dataProvider.getList();
		for (Song s : repList) {
			RepertoireSong rs = new RepertoireSong(s);
			list.add(rs);
		}
	}

	public Widget getWidget() {
		return cellList;
	}

	public Song getSelectedSong() {
		return selectionModel.getSelectedObject();
	}

	public void addSongPerformance(Song song) {
		if (song instanceof RepertoireSong) {
			((RepertoireSong) song).addToPerformance();
		}
	}

	public void removeSongPerformance(Song song) {
		if (song instanceof RepertoireSong) {
			((RepertoireSong) song).removeFromPermance();
		}
	}

	/*
	 * public void addClickListener(Object listener) {
	 * 
	 * }
	 * 
	 * public void addDblClickListener(Object listener) {
	 * 
	 * }
	 */
	static class SongCell extends AbstractCell<RepertoireSong> {
		public SongCell() {
			super("click", "dblclick");
		}

		/*-
		@Override
		public void onBrowserEvent(Context context, Element parent, RepertoireSong value, NativeEvent event,
				ValueUpdater<RepertoireSong> valueUpdater) {
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		
			if ("click".equals(event.getType())) {
		
			} else if ("dblclick".equals(event.getType())) {
				
			}
		}
		 */

		interface Templates extends SafeHtmlTemplates {
			@SafeHtmlTemplates.Template("<div style=\"{0}\">{1}</div>")
			SafeHtml cell(SafeStyles styles, SafeHtml value);
		}

		private static Templates templates = GWT.create(Templates.class);

		@Override
		public void render(Context context, RepertoireSong value, SafeHtmlBuilder sb) {
			if (value == null)
				return;
			// If the value comes from the user, we escape it to avoid XSS attacks.
			SafeHtml safeValue = SafeHtmlUtils.fromString(value.getTitleAndDuration());

			// Use the template to create the Cell's html.
			SafeStyles styles = SafeStylesUtils.forTrustedColor(getColorFromPerformances(value));
			SafeHtml rendered = templates.cell(styles, safeValue);
			sb.append(rendered);
		}

		private String getColorFromPerformances(RepertoireSong value) {
			if (value.getNrTimesInPerformance() == 0) {
				return "#ffffff";
			}
			return "#808080";
		}
	}

	static class RepertoireSong extends Song {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7582781073785540978L;

		private int nrTimesInPerformance = 0;

		public RepertoireSong() {
			super();
		}

		public RepertoireSong(Song fromSong) {
			super(fromSong.getTitle(), fromSong.getLength(), fromSong.getInRepertoireSince(),
					fromSong.isChristmasSong());
		}

		public void addToPerformance() {
			nrTimesInPerformance++;
		}

		public void removeFromPermance() {
			if (nrTimesInPerformance > 0) {
				nrTimesInPerformance--;
			}
		}

		public int getNrTimesInPerformance() {
			return nrTimesInPerformance;
		}
	}
}
