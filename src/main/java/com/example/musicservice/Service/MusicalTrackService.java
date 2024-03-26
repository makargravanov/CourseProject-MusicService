package com.example.musicservice.Service;

import com.example.musicservice.Data.MusicalTrack;
import com.example.musicservice.Data.MusicalTrackUpload;
import com.example.musicservice.Mapper.Mapper;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class MusicalTrackService {
    private final MusicalTrackServiceDB musicalTrackServiceDB;

    public MusicalTrackService(MusicalTrackServiceDB musicalTrackServiceDB) {
        this.musicalTrackServiceDB = musicalTrackServiceDB;
    }

    @Async
    public CompletableFuture<ResponseEntity<MusicalTrack>> getTrackById(Long id) {
        try {
            CompletableFuture<MusicalTrack> track = musicalTrackServiceDB.getMusicalTrackById(id);
            MusicalTrack out = track.get();
            return CompletableFuture.completedFuture(ResponseEntity.ok(out));
        } catch (ExecutionException e){
            System.out.println("(getTrackById)Перехват исключения" + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        } catch (InterruptedException e) {
            System.out.println("IEE в getTrackById " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }
    }

    @Async
    public CompletableFuture<ResponseEntity<ArrayList<MusicalTrack>>> getTrackByName(String name, int page, int size) {
        try {
            CompletableFuture<ArrayList<MusicalTrack>> track = musicalTrackServiceDB.getMusicalTrackByName(name, page, size);
            ArrayList<MusicalTrack> out = track.get();
            return CompletableFuture.completedFuture(ResponseEntity.ok(out));
        } catch (ExecutionException e){
            System.out.println("(getTrackByName) Перехват исключения " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        } catch (InterruptedException e) {
            System.out.println("IEE " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }
    }

    @Async
    public CompletableFuture<ResponseEntity<ArrayList<MusicalTrack>>> getMusicalTrackByGenre(String genre, int page, int size) {
        try {
            CompletableFuture<ArrayList<MusicalTrack>> track = musicalTrackServiceDB.getMusicalTrackByGenre(genre, page, size);
            ArrayList<MusicalTrack> out = track.get();
            return CompletableFuture.completedFuture(ResponseEntity.ok(out));
        } catch (ExecutionException e){
            System.out.println("(getMusicalTrackByGenre) Перехват исключения " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        } catch (InterruptedException e) {
            System.out.println("IEE " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }
    }

    @Async
    public CompletableFuture<ResponseEntity<ArrayList<MusicalTrack>>> getMusicalTrackByNameOrderByGenre(String name, String genre, int page, int size) {
        try {
            CompletableFuture<ArrayList<MusicalTrack>> track = musicalTrackServiceDB.getMusicalTrackByNameOrderByGenre(name, genre, page, size);
            ArrayList<MusicalTrack> out = track.get();
            return CompletableFuture.completedFuture(ResponseEntity.ok(out));
        } catch (ExecutionException e){
            System.out.println("(getMusicalTrackByNameOrderByGenre) Перехват исключения " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        } catch (InterruptedException e) {
            System.out.println("IEE " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }
    }


    private CompletableFuture<String> saveFile(MultipartFile file, String name) {
        try{
        if (!Objects.equals(file.getContentType(), "audio/mp3")) {
            String path = "**/resources/static/MusicalTracks/" + UUID.randomUUID().toString() + ".mp3";
            file.transferTo(new java.io.File(path));
            return CompletableFuture.completedFuture(path);
        }else if (!Objects.equals(file.getContentType(), "text/txt")) {
            String path = "**/resources/static/Lyrics/" + UUID.randomUUID().toString() + ".txt";
            file.transferTo(new java.io.File(path));
            return CompletableFuture.completedFuture(path);
        }else if (!Objects.equals(file.getContentType(), "image/jpg")) {
            String path = "**/resources/static/Images/CoverImages/" + UUID.randomUUID().toString() + ".jpg";
            file.transferTo(new java.io.File(path));
            return CompletableFuture.completedFuture(path);
        }else {
            throw new IllegalArgumentException("Incorrect file type");
        }
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Incorrect file type"+ e.getMessage());
        }
        catch (IOException e) {
            throw new RuntimeException("I/O error"+ e.getMessage());
        }
    };
    @Async
    public CompletableFuture<ResponseEntity<?>> saveNewTrack(MultipartFile file, MultipartFile lyricsFile, MultipartFile imageFile, MusicalTrackUpload trackUpload) {
        try {
            MusicalTrack trackData = Mapper.uploadToData(trackUpload);
            String name = trackData.getName();

            String pathToFile = saveFile(file, name).get();
            String pathToLyricsFile = saveFile(lyricsFile, name).get();
            String pathToImageFile = saveFile(imageFile, name).get();

            trackData.setPathToFile(pathToFile);
            trackData.setPathToLyrics(pathToLyricsFile);
            trackData.setPathToImage(pathToImageFile);

            trackData.setAuditions(0L);
            trackData.setReleaseDate(LocalDateTime.now());

            musicalTrackServiceDB.saveMusicalTrack(trackData);
            return CompletableFuture.completedFuture(ResponseEntity.ok().build());

        } catch (ExecutionException e) {
            System.out.println("(saveTrack or saveFile(?)) Перехват исключения " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.internalServerError().build());
        } catch (InterruptedException e) {
            System.out.println("IEE " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.internalServerError().build());
        } catch (RuntimeException e) {
            System.out.println("(saveFile) Перехват исключения " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.internalServerError().build());
        }

    }

    //TODO-add deleteTrack & updateTrack
    //Реализации deleteTrack и updateTrack так же будут,но позже.
    //Обязательно сделать проверку на то, чтобы у удаляющего было право на удаление этого трека
    //(то есть, это должен быть исполнитель, добавивший трек).

}