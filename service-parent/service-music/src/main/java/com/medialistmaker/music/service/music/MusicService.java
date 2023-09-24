package com.medialistmaker.music.service.music;

import com.medialistmaker.music.domain.Music;
import com.medialistmaker.music.exception.badrequestexception.CustomBadRequestException;
import com.medialistmaker.music.exception.entityduplicationexception.CustomEntityDuplicationException;
import com.medialistmaker.music.exception.notfoundexception.CustomNotFoundException;

import java.util.List;

public interface MusicService {

    List<Music> browseByIds(List<Long> musicIds);

    List<Music> browseByType(Integer type);

    Music readById(Long musicId) throws CustomNotFoundException;

    Music readByApiCode(String apiCode) throws CustomNotFoundException;

    Music add(Music music) throws CustomBadRequestException, CustomEntityDuplicationException;

    Music deleteById(Long id) throws CustomNotFoundException;

}