package com.medialistmaker.music.controller.deezerapi;

import com.medialistmaker.music.connector.deezer.album.DeezerAlbumConnectorProxy;
import com.medialistmaker.music.connector.deezer.search.DeezerSearchConnectorProxy;
import com.medialistmaker.music.dto.externalapi.deezerapi.AlbumElementDTO;
import com.medialistmaker.music.dto.externalapi.deezerapi.TrackListDTO;
import com.medialistmaker.music.dto.externalapi.deezerapi.search.list.AlbumSearchListDTO;
import com.medialistmaker.music.exception.badrequestexception.CustomBadRequestException;
import com.medialistmaker.music.exception.servicenotavailableexception.ServiceNotAvailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/musics/deezerapi/albums")
public class AlbumController {

    @Autowired
    DeezerSearchConnectorProxy albumSearchConnectorProxy;

    @Autowired
    DeezerAlbumConnectorProxy albumConnectorProxy;

    @GetMapping
    public ResponseEntity<AlbumSearchListDTO> getByAlbumName(@RequestParam("name") String albumName)
            throws CustomBadRequestException {

        return new ResponseEntity<>(
                this.albumSearchConnectorProxy.getAlbumByQuery(albumName),
                HttpStatus.OK
        );

    }

    @GetMapping("/apicodes/{apicode}")
    public ResponseEntity<AlbumElementDTO> getByApiCode(@PathVariable("apicode") String apiCode)
            throws CustomBadRequestException, ServiceNotAvailableException {

        return new ResponseEntity<>(this.albumConnectorProxy.getByApiCode(apiCode), HttpStatus.OK);

    }

    @GetMapping("/apicodes/{apicode}/tracklist")
    public ResponseEntity<TrackListDTO> getTrackListByAlbumApiCode(@PathVariable("apicode") String apiCode)
            throws ServiceNotAvailableException, CustomBadRequestException {

        return new ResponseEntity<>(this.albumConnectorProxy.getTrackListByAlbumApiCode(apiCode), HttpStatus.OK);

    }
}