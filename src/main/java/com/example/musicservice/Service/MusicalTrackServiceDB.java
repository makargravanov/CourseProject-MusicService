package com.example.musicservice.Service;

import com.example.musicservice.Data.MusicalTrack;

import com.example.musicservice.Entity.MusicalTrackEntity;
import com.example.musicservice.Mapper.Mapper;
import com.example.musicservice.Repository.MusicalTrackRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class MusicalTrackServiceDB {
    private final MusicalTrackRepository musicalTrackRepository;
    public MusicalTrackServiceDB(MusicalTrackRepository musicalTrackRepository) {
        this.musicalTrackRepository = musicalTrackRepository;
    }


    @Async
    protected CompletableFuture<Long> saveMusicalTrack(MusicalTrack musicalTrack){
        MusicalTrackEntity savedEntity = musicalTrackRepository.save(Mapper.dataToEntity(musicalTrack));
        return CompletableFuture.completedFuture(savedEntity.getId());
    }
    @Async
    protected void deleteMusicalTrackById(Long id)throws NullPointerException{
       Optional<MusicalTrackEntity> opt = musicalTrackRepository.findById(id);
       if(opt.isPresent()){
            musicalTrackRepository.deleteById(id);
        }else{
           throw new NullPointerException("No results found for id: " + id);
        }
    }
    @Async
    protected CompletableFuture<MusicalTrack> getMusicalTrackById(Long id) throws IllegalArgumentException, NullPointerException{
        if(id==null) throw new IllegalArgumentException("id cannot be null");
        Optional<MusicalTrackEntity> opt = musicalTrackRepository.findById(id);
        if(opt.isPresent()){
            return CompletableFuture.completedFuture(Mapper.entityToData(opt.get()));
        }else{
            throw new NullPointerException("No results found for id: " + id);
        }
    }
    @Async
    protected CompletableFuture<ArrayList<MusicalTrack>> getMusicalTrackByGenre(String genre, int page, int size) throws NullPointerException {
        Sort sort = Sort.by(Sort.Direction.ASC, "genre");
        PageRequest pageable = PageRequest.of(page, size, sort);

        CompletableFuture<Page<MusicalTrackEntity>> pageResultFuture = CompletableFuture.supplyAsync(() -> {
            Page<MusicalTrackEntity> pageResult = musicalTrackRepository.findByGenre(genre, pageable).join(); // Extracting the Page from CompletableFuture
            if (pageResult.isEmpty()) {
                throw new NullPointerException("No results found for genre: " + genre);
            }
            return pageResult;
        });

        return pageResultFuture.thenApply(pageResult -> {
            ArrayList<MusicalTrackEntity> musicalTrackEntities = new ArrayList<>(pageResult.getContent());
            return Mapper.entityToData(musicalTrackEntities);
        });
    }
    @Async
    protected CompletableFuture<ArrayList<MusicalTrack>> getMusicalTrackByName(String name,int page, int size) throws NullPointerException {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        PageRequest pageable = PageRequest.of(page, size, sort);

        CompletableFuture<Page<MusicalTrackEntity>> pageResultFuture = CompletableFuture.supplyAsync(() -> {
            Page<MusicalTrackEntity> pageResult = musicalTrackRepository.findByName(name, pageable).join(); // Extracting the Page from CompletableFuture
            if (pageResult.isEmpty()) {
                throw new NullPointerException("No results found for name: " + name);
            }
            return pageResult;
        });

        return pageResultFuture.thenApply(pageResult -> {
            ArrayList<MusicalTrackEntity> musicalTrackEntities = new ArrayList<>(pageResult.getContent());
            return Mapper.entityToData(musicalTrackEntities);
        });
    }
    @Async
    protected CompletableFuture<ArrayList<MusicalTrack>> getMusicalTrackByNameOrderByGenre(String name,String genre, int page, int size) throws NullPointerException {
        Sort sort = Sort.by(Sort.Order.asc("name"), Sort.Order.asc("genre"));
        PageRequest pageable = PageRequest.of(page, size, sort);

        CompletableFuture<Page<MusicalTrackEntity>> pageResultFuture = CompletableFuture.supplyAsync(() -> {
            Page<MusicalTrackEntity> pageResult = musicalTrackRepository.findByNameOrderByGenre(name, genre, pageable).join(); // Extracting the Page from CompletableFuture
            if (pageResult.isEmpty()) {
                throw new NullPointerException("No results found for name order by genre. name::" + name + ", genre::" + genre);
            }
            return pageResult;
        });

        return pageResultFuture.thenApply(pageResult -> {
            ArrayList<MusicalTrackEntity> musicalTrackEntities = new ArrayList<>(pageResult.getContent());
            return Mapper.entityToData(musicalTrackEntities);
        });
    }
    @Async
    protected void updateMusicalTrack(MusicalTrack musicalTrack) throws IllegalArgumentException, NullPointerException{
        if(musicalTrack.getId()==null) throw new IllegalArgumentException("id cannot be null");
        Optional<MusicalTrackEntity> opt = musicalTrackRepository.findById(musicalTrack.getId());
        if(opt.isPresent()){
            musicalTrackRepository.save(Mapper.dataToEntity(musicalTrack));
        }else{
            throw new NullPointerException("Track for update not found!");
        }
    }
}
