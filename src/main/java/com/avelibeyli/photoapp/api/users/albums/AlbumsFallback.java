package com.avelibeyli.photoapp.api.users.albums;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class AlbumsFallback implements AlbumsServiceClient {
    @Override
    public List<AlbumResponseModel> getAlbums(String id) {
        return new ArrayList<>();
    }
}