package com.nft.platform.mapper;

import com.nft.platform.domain.Celebrity;
import com.nft.platform.dto.request.CelebrityRequestDto;
import com.nft.platform.dto.response.CelebrityResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({SpringExtension.class})
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        CelebrityMapperImpl.class
})
public class CelebrityMapperTest {

    @Autowired
    private CelebrityMapper celebrityMapper;

    @Test
    @DisplayName("Update Celebrity from request Dto")
    void updateCelebrityFromRequestDto() {
        Celebrity celebrity = createDummyCelebrity();
        CelebrityRequestDto celebrityRequestDto = createDummyCelebrityRequestDto();
        celebrity = celebrityMapper.toEntity(celebrityRequestDto, celebrity);
        assertEquals(celebrityRequestDto.getImageUrl(), celebrity.getImageUrl());
        assertEquals(celebrityRequestDto.getName(), celebrity.getName());
    }

    @Test
    @DisplayName("Convert Celebrity to Response Dto")
    void mapCelebrityToResponseDto() {
        Celebrity celebrity = createDummyCelebrity();
        CelebrityResponseDto dto = celebrityMapper.toDto(celebrity);
        assertEquals(celebrity.getImageUrl(), dto.getImageUrl());
        assertEquals(celebrity.getName() + " " + celebrity.getLastName(), dto.getName());
    }

    private Celebrity createDummyCelebrity() {
        return Celebrity.builder()
                .imageUrl("image")
                .name("name")
                .lastName("lastname")
                .build();
    }

    private CelebrityRequestDto createDummyCelebrityRequestDto() {
        return CelebrityRequestDto.builder()
                .imageUrl("image-up")
                .name("name-up")
                .build();
    }
}
