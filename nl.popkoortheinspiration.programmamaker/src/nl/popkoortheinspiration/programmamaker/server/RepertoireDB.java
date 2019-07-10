package nl.popkoortheinspiration.programmamaker.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import nl.popkoortheinspiration.programmamaker.client.IRepertoireDB;
import nl.popkoortheinspiration.programmamaker.shared.Song;

public class RepertoireDB extends RemoteServiceServlet implements IRepertoireDB {
	private Song[] songs = new Song[0];
	private String csvFilename = "ProgrammasArchief.csv";
	/**
	 * 
	 */
	private static final long serialVersionUID = 729072419445431193L;

	@Override
	public Song[] getSongs() {
		return songs;
	}

	public RepertoireDB() {
		ArrayList<Song> songList = new ArrayList<Song>();
		FileReader rdr;
		try {
			rdr = new FileReader(csvFilename);
			BufferedReader lineReader = new BufferedReader(rdr);
			while (lineReader.ready()) {
				String line = lineReader.readLine();
				String[] fields = line.split("\"?\\s*,\\s*\"?");
				if (fields.length >= 4) {
					String idx = fields[0];
					String title = fields[1];
					String[] inRepFields = fields[2].split("-");
					String[] durationFields = fields[3].split(":");

					if (idx.matches("[0-9]+")) {
						boolean xmas = title.toLowerCase().contains("christmas");
						int length = Integer.parseInt(durationFields[0]) * 60 + Integer.parseInt(durationFields[1]);
						GregorianCalendar inRepSince = new GregorianCalendar(Integer.parseInt(inRepFields[2]),
								Integer.parseInt(inRepFields[1]), Integer.parseInt(inRepFields[0]));

						Song s = new Song(title, length, inRepSince.getTime(), xmas);
						songList.add(s);
					}
				}
			}
			lineReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		songs = songList.toArray(songs);
	}

}
