package com.nft.platform.feign.client;

import com.nft.platform.common.enums.FileType;
import com.nft.platform.feign.client.dto.FileInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
        name = FileServiceClient.NAME,
        url = "${feign." + FileServiceClient.NAME + ".url}"
)
public interface FileServiceClient {

    String NAME = "nft-file-service";
    String path = "/api/v1/files";

    @PostMapping(value = path, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<FileInfoResponseDto> fileUpload(@RequestParam FileType fileType, @RequestPart(value = "file") MultipartFile file);

    @DeleteMapping(value = path)
    void deleteFile(@RequestParam("url") String url);

}
