package com.example.musicservice.Controller;

import com.example.musicservice.Data.MusicalTrack;
import com.example.musicservice.Data.MusicalTrackUpload;
import com.example.musicservice.Data.RequestGetTracksByName;
import com.example.musicservice.Data.RequestId;
import com.example.musicservice.Service.MusicalTrackService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/music")
public class MusicalServiceController {

    private final MusicalTrackService musicalTrackService;

    public MusicalServiceController(MusicalTrackService musicalTrackService) {
        this.musicalTrackService = musicalTrackService;
    }


    @PostMapping("/getTrackById")
    public ResponseEntity<MusicalTrack> getTrackByIdController(Long id) {
        System.out.println("getTrackById id= " + id.toString());
        return musicalTrackService.getTrackById(id);
    }

    @PostMapping("/track/musicFile")
    public ResponseEntity<Resource> downloadAudioFile(@RequestBody RequestId id) {
        System.out.println("id= " + id.getId().toString());
        return musicalTrackService.getTrackFileById(id.getId());
    }
    @GetMapping("/track/imageFile/{id}")
    public ResponseEntity<Resource> getImageFileById(@PathVariable Long id) {
        return musicalTrackService.getImageById(id);
    }

    @GetMapping("/track/lyricsFile/{id}")
    public ResponseEntity<Resource> getLyricsFileByIdController(@PathVariable Long id) {
        return musicalTrackService.getLyricsFileById(id);
    }
    @Async
    @PostMapping(value = "/uploadTrack", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompletableFuture<ResponseEntity<?>> uploadTrack(@ModelAttribute MusicalTrackUpload track,
                                                            @RequestParam("trackFile") MultipartFile trackFile,
                                                            @RequestParam("lyricsFile") MultipartFile lyricsFile,
                                                            @RequestParam("imageFile") MultipartFile imageFile) {
        if (!Objects.equals(trackFile.getContentType(), "audio/mpeg")) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Only mp3 files are allowed"));
        }
        if (!Objects.equals(lyricsFile.getContentType(), "text/plain")) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Only txt files are allowed"));
        }
        if (!Objects.equals(imageFile.getContentType(), "image/jpeg")) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Only jpg files are allowed"));
        }

        return musicalTrackService.saveNewTrack(trackFile, lyricsFile, imageFile, track);
    }


    @GetMapping("/test")
    public String testController() {
        return "Test endpoint accessed successfully";
    }

    @PostMapping("/getTracksByName")
    public ResponseEntity<ArrayList<MusicalTrack>> getTrackByNameController(@RequestBody RequestGetTracksByName request) {
        return musicalTrackService.getTrackByName(request.getName(), request.getPage(), 10);
    }
}
