package vn.tqt.player.music.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.json.simple.parser.ParseException;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.SAXException;
import vn.tqt.player.music.PlayerApp;
import vn.tqt.player.music.repository.Playlist;
import vn.tqt.player.music.repository.Song;
import vn.tqt.player.music.initWindow.InitAlertWindow;
import vn.tqt.player.music.services.GetTitleSong;
import vn.tqt.player.music.services.jsonFile.JacksonParser;
import vn.tqt.player.music.services.jsonFile.Json;

public class PlayerController implements Initializable {
    @FXML
    private  Slider songTimeSlider;
    @FXML
    private AnchorPane pane;
    @FXML
    private TextField namePlaylist;
    @FXML
    private ImageView logoSong;
    @FXML
    private Label singerName;
    @FXML
    private Label songName;
    @FXML
    private Label songTime;
    @FXML
    private Button playButton, pauseButton, nextButton, perviousButton, loopButton, randomButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private ComboBox<String> playlistBox;
    @FXML
    private Slider volumeBar;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private TableView<Song> table;
    @FXML
    private TableColumn<Song, Integer> idColumn;
    @FXML
    private TableColumn<Song, String> nameColumn;
    private ObservableList<Song> songList;
    private ObservableList<Playlist> playlistList;

    private Media media;
    private MediaPlayer mediaPlayer;

    @FXML
    private File musicDirectory;
    @FXML
    private File allMusicDirectory;
    @FXML
    private File allMusicInPlaylist;
    @FXML
    private File imageDirectory;
    private File[] musicFiles;
    private File[] allMusicFiles;
    private File[] allMusicFilesInPlayList;

    private File[] imageFiles;
    private ArrayList<String> songs;
    private ArrayList<String> allSong;
    private ArrayList<File> songInPlaylist;
    private ArrayList<File> allSongInPlaylist;
    private ArrayList<File> images;
    private int songNumber;
    private int[] speeds = {75, 100, 125, 150, 175, 500000};
    private Timer timer;
    private TimerTask task;
    private boolean running;
    private boolean playBtnStatus;
    private boolean randomBtnStatus;
    private boolean loopBtnStatus;
    private String sourcePathMusic = "music";


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        importMusicDirectory();
        try {
            initInfoSong();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
//
//        table.setRowFactory( tv -> {
//            TableRow<Song> row = new TableRow<>();
//            row.setOnMouseClicked(event -> {
//                if (event.getClickCount() == 3 && (! row.isEmpty()) ) {
//                    Song rowData = row.getItem();
//                    System.out.println(rowData.getSongName());
//                }
//            });
//            return row ;
//        });
    }
        

    public void initInfoSong() throws IOException, ParseException {
        importImagesDirectory();
        initSpeedBox();
        initVolumeBar();
        initTableviewSong();
        getSongTitle();
        importPlaylist();
        showPlaylistOnComBox();

        try {
            setLogoSong();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            initMedia(songNumber);
            addSongToTbView();
        } catch (TikaException | IOException | SAXException e) {
            e.printStackTrace();
        }

    }
    public void importMusicDirectory() {
        songs = new ArrayList<>();
        musicDirectory = new File(sourcePathMusic);
        musicFiles = musicDirectory.listFiles();
        if (musicFiles != null) {
            for (File file : musicFiles) {
                songs.add(file.getPath());
            }
        }
    }

    public void importImagesDirectory() {
        images = new ArrayList<>();
        imageDirectory = new File("image");
        imageFiles = imageDirectory.listFiles();
        if (imageFiles != null) {
            for (File file : imageFiles) {
                images.add(file);
            }
        }
    }
   public void importPlaylist() throws IOException, ParseException {
        String dataReader = Json.readFile("data/playlist.json");
        Playlist.listPlaylists = JacksonParser.INSTANCE.toList(dataReader,Playlist.class);
        playlistList.addAll(Playlist.listPlaylists);
   }
    public void initSpeedBox() {
        speedBox.getItems().clear();
        for (int i = 0; i < speeds.length; i++) {
            speedBox.getItems().add(Integer.toString(speeds[i]));
        }
        speedBox.setOnAction(this::changeSpeed);
    }

    public void initVolumeBar() {
        volumeBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(volumeBar.getValue() * 0.01);
            }
        });
    }

    public void initTableviewSong() {
        songList = FXCollections.observableArrayList();
        idColumn.setCellValueFactory(new PropertyValueFactory<Song, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("songName"));
        table.setItems(songList);
        playlistList = FXCollections.observableArrayList();
        namePlaylist.setPromptText("Enter Name Playlist Want Creat");
    }

    public Playlist getPlaylistObjeto(String name){
        for (int i = 0; i < Playlist.listPlaylists.size(); i++) {
            if (Playlist.listPlaylists.get(i).getName().equals(name)){
                return Playlist.listPlaylists.get(i);
            }
        }
        return null;
    }

    public void showAllSong()  {
        songList.clear();
        allSong = new ArrayList<>();
        allMusicDirectory = new File(sourcePathMusic);
        allMusicFiles = allMusicDirectory.listFiles();
        if (allMusicFiles != null) {
            for (File file : allMusicFiles) {
                allSong.add(file.getPath());
            }
        }
        for (int i = 0; i < allSong.size(); i++) {
            String songName = GetTitleSong.get(i, allSong);
            Song newSong = new Song();
            newSong.setId(i + 1);
            newSong.setSongName(songName);
            newSong.setSongPath(allSong.get(i));
            songList.add(newSong);
        }
    }

    public void addSongToTbView() throws TikaException, IOException, SAXException {
        for (int i = 0; i < songs.size(); i++) {
            String songName = GetTitleSong.get(i, songs);
            Song newSong = new Song();
            newSong.setId(i + 1);
            newSong.setSongName(songName);
            newSong.setSongPath(songs.get(i));
            songList.add(newSong);
        }
    }

    public void showPlaylistOnComBox() {
        playlistBox.getItems().clear();
        playlistBox.setPromptText("Select PlayList");
        for (int j = 0; j < playlistList.size(); j++) {
            playlistBox.getItems().add(playlistList.get(j).getName());
        }
    }

    public String getSongTitle() {
        File musicFile = new File(songs.get(songNumber));
        return musicFile.getName();

    }

    public void getSongInfo(int songIndex) {
        String fileLocation = songs.get(songIndex);
        try {
            InputStream input = new FileInputStream(fileLocation);
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input, handler, metadata, parseCtx);
            input.close();
            songName.setText(metadata.get("title"));
            singerName.setText(metadata.get("xmpDM:artist"));
        } catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
        }
    }

    public void initMedia(int songIndex) throws TikaException, IOException, SAXException {

        File musicFile = new File(songs.get(songIndex));
        media = new Media(musicFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        getSongInfo(songNumber);
        setLogoSong();
    }

    public String getRelativeName() {
        String songNamePath = getSongTitle();
        String relativeSongName = songNamePath.substring(0, songNamePath.length() - 4);
        return relativeSongName + ".jpg";
    }

    public void setLogoSong() throws MalformedURLException {
        File file = new File("src/main/resources/vn/tqt/player/music/image/" + getRelativeName());
        String localUrl = file.toURI().toURL().toString();
        Image image = new Image(localUrl);
        logoSong.setImage(image);
    }

    public void playSong()  {
        if (playBtnStatus) {
            mediaPlayer.pause();
            cancelTimer();
            playBtnStatus = false;
            playButton.setText("Play");
        } else {
            mediaPlayer.play();
            getSongInfo(songNumber);
            resetVolumeAndSpeed();
            beginTimer();
            playBtnStatus = true;
            playButton.setText("Pause");
        }
    }

public void resetVolumeAndSpeed(){
    volumeBar.setValue(75);
    speedBox.setValue("100");
}

    public void nextSong() throws IOException, TikaException, SAXException {
        if (songNumber < songs.size() - 1) {
            if (playBtnStatus) {
                playBtnStatus = false;
                songNumber++;
                mediaPlayer.stop();
                initMedia(songNumber);
                playSong();
            } else {
                songNumber++;
                mediaPlayer.stop();
                initMedia(songNumber);
                playSong();
            }
        } else {
            if (playBtnStatus) {
                playBtnStatus = false;
                songNumber = 0;
                mediaPlayer.stop();
                initMedia(songNumber);
                playSong();
                System.out.println(songs.size());
                System.out.println(songNumber);
            } else {
                songNumber = 0;
                mediaPlayer.stop();
                initMedia(songNumber);
                playSong();
            }
        }
    }

    public void perviousSong() throws IOException, TikaException, SAXException {
        if (songNumber > 0) {
            if (playBtnStatus) {
                playBtnStatus = false;
                songNumber--;
                mediaPlayer.stop();
                initMedia(songNumber);
                playSong();
            } else {
                songNumber--;
                mediaPlayer.stop();
                initMedia(songNumber);
            }
        }
    }


    public void loopSong() {
        if (!loopBtnStatus) {
            loopBtnStatus = true;
            mediaPlayer.setOnEndOfMedia(new Runnable() {
                public void run() {
                    mediaPlayer.seek(Duration.ZERO);
                }
            });
            loopButton.setText("looping");
            if (randomBtnStatus) {
                randomBtnStatus = false;
                randomButton.setText("random");
            }
        } else {
            loopBtnStatus = false;
            loopButton.setText("loop");
        }

    }

    public void randomSong() {
        if (!randomBtnStatus) {
            randomBtnStatus = true;
            randomButton.setText("randoming");
            if (loopBtnStatus) {
                loopBtnStatus = false;
                loopButton.setText("loop");
            }
        } else {
            randomBtnStatus = false;
            randomButton.setText("random");
        }
    }

    public int randomSongNumber() {
        if (randomBtnStatus) {
            int max = songs.size() - 1;
            return (int) Math.floor(Math.random() * (max + 1));
        }
        return -1;
    }

    public void playRandomSong() throws TikaException, IOException, SAXException {
        mediaPlayer.stop();
        songNumber = randomSongNumber();
        initMedia(songNumber);
        mediaPlayer.play();
        getSongInfo(songNumber);
        beginTimer();
    }

    public void changeSpeed(ActionEvent event) {
        mediaPlayer.setRate(Integer.parseInt(speedBox.getValue()) * 0.01);
    }

    public void beginTimer() {
        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                running = true;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        double current = mediaPlayer.getCurrentTime().toSeconds();
                        double end = media.getDuration().toSeconds();
                        double percenttime = (current /  end) * 100;
                        int second = (int) current % 60;
                        int minute = (int) (current / 60) % 60;
                        String minutes = String.valueOf(minute);
                        String seconds = String.valueOf(second);
                        songTime.setText(minutes + ":" + seconds);
                        songProgressBar.setProgress(current / end);
                        if (current / end == 1) {
                            if (randomBtnStatus) {
                                try {
                                    playRandomSong();
                                } catch (TikaException | IOException | SAXException e) {
                                    e.printStackTrace();
                                }
                            }
                            cancelTimer();
                            try {
                                nextSong();
                            } catch (IOException | TikaException | SAXException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void cancelTimer() {
        running = false;
        timer.cancel();
    }

    public boolean checkDuplicatePlaylist(String folderPlaylistName) {
        for (Playlist playlist : playlistList) {
            if (playlist.getName().trim().equals(folderPlaylistName.trim())) {
                return false;
            }
        }
        return true;
    }

    public void createPlaylist() throws Exception {
        String folderPlaylistName = namePlaylist.getText();
        String titleAlert = "Player Information";
        String contentAlert = "Playlist \"" + folderPlaylistName + "\" already exist";
        if (checkDuplicatePlaylist(folderPlaylistName)){
            Playlist newPlaylist = new Playlist();
            newPlaylist.setName(folderPlaylistName);
            playlistList.add(newPlaylist);
            Playlist.listPlaylists.add(newPlaylist);
            String jsonPlaylist =  JacksonParser.INSTANCE.toJson(Playlist.listPlaylists);
            Json.writeFile(jsonPlaylist,"data/playlist.json");
            namePlaylist.setText("");
            int sizePlaylist = playlistList.size();
            playlistBox.getItems().add(playlistList.get(sizePlaylist - 1).getName());
        } else {
            namePlaylist.setText("");
            InitAlertWindow.initAlert(titleAlert,contentAlert);
        }
    }

    public void playThisList() throws IOException, ParseException {
            String playlistName = playlistBox.getValue();
            if (playlistName != null){
                if (!checkQuantytiSongInPlayList(playlistName)){
                    Playlist newPlaylist = getPlaylistObjeto(playlistName);
                    songs.clear();
                    songs.addAll(newPlaylist.getListSong());
                    mediaPlayer.pause();
                    initInfoSong();
                    playSong();
                    playlistBox.setValue(playlistName);
                } else {
                    String titleAlert = "Player Warring!!";
                    String contentAlert = "The playlist is empty, please add music to the playlist to play music!";
                    InitAlertWindow.initAlert(titleAlert,contentAlert);
                }
            } else{
                String titleAlert = "Player Alert!!";
                String contentAlert = "No playlists, create playlists";
                InitAlertWindow.initAlert(titleAlert,contentAlert);
            }
    }

    public void addToPlayList() throws IOException {
        Song selected = table.getSelectionModel().getSelectedItem();
        String songPath = selected.getSongPath();
        String playlistName = playlistBox.getValue();
        if (!checkSongInPlaylist(playlistName,songPath)) {
            Playlist newPlaylist = getPlaylistObjeto(playlistName);
            newPlaylist.getListSong().add(songPath);
            String jsonPlaylist =  JacksonParser.INSTANCE.toJson(Playlist.listPlaylists);
            Json.writeFile(jsonPlaylist,"data/playlist.json");
            String titleAlert = "ADD Song To Playlist";
            String contentAlert = "Add Song \"" + selected.getSongName() + "\" to Playlist \"" + playlistName + "\"" + "DONE";
            InitAlertWindow.initAlert(titleAlert, contentAlert);
        } else {
            String titleAlert2 = "Player Alerrt!";
            String contentAlert2 = selected.getSongName() + " already exists in, cannot be added";
            InitAlertWindow.initAlert(titleAlert2, contentAlert2);
        }
    }

    public boolean checkQuantytiSongInPlayList(String playlistName) {
        Playlist playlist = getPlaylistObjeto(playlistName);
        if (playlist.getListSong().isEmpty()) {
            return true;
        }
        return false;
    }

   public boolean checkSongInPlaylist(String playlistName, String pathSong){
       Playlist newPlaylist = getPlaylistObjeto(playlistName);
            for (int i = 0; i < newPlaylist.getListSong().size(); i++) {
                if (newPlaylist.getListSong().get(i).equals(pathSong)){
                return true;
           }
       }
       return false;
   }

    public void deletePlaylist() throws IOException {
        String playlistName = playlistBox.getValue();
        Playlist newPlaylist = getPlaylistObjeto(playlistName);
        for (int i = 0; i < Playlist.listPlaylists.size(); i++) {
            if(Playlist.listPlaylists.get(i).equals(newPlaylist)){
             Playlist.listPlaylists.remove(i);
             playlistList.remove(i);
            }
        }
        showPlaylistOnComBox();
        String jsonPlaylist =  JacksonParser.INSTANCE.toJson(Playlist.listPlaylists);
        Json.writeFile(jsonPlaylist,"data/playlist.json");
    }

    public void chooseFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) pane.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        sourcePathMusic = selectedDirectory.getPath();
        mediaPlayer.stop();
        initialize(null, null);
    }

    public void logOut(ActionEvent event) throws IOException {
        mediaPlayer.stop();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(PlayerApp.class.getResource("login-view.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }
    // Ghi đè equal để so sánh hai object
}