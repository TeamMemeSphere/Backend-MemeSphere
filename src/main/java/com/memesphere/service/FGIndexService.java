package com.memesphere.service;

import com.memesphere.apipayload.code.status.ErrorStatus;
import com.memesphere.apipayload.exception.GeneralException;
import com.memesphere.domain.FGIndex;
import com.memesphere.repository.FGIndexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FGIndexService {

    private final FGIndexRepository fgIndexRepository;

    public FGIndex getIndexByDate(LocalDate date) {
        return fgIndexRepository.findByDate(date)
                .orElseThrow(() -> new GeneralException(ErrorStatus.DATE_NOT_FOUND));
    }

}
