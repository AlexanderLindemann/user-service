package com.nft.platform.util;

import org.testcontainers.utility.DockerImageName;

public interface TestUtil {
    String DOCKER_REGISTRY = "dockerhub.nexus.dcloud.tech/";

    static String getDockerImage(String imageName) {
        return DOCKER_REGISTRY + imageName;
    }

    static DockerImageName getTesContainersDockerImage(String imageName) {
        return DockerImageName.parse(getDockerImage(imageName));
    }
}
