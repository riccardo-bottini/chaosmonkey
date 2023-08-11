package com.riccardobottini.chaosmonkey;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Map;
import static java.net.HttpURLConnection.HTTP_OK;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodListBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;

@SpringBootTest
@EnableKubernetesMockClient(crud = true)
class ChaosmonkeyApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(ChaosmonkeyApplicationTests.class);

	KubernetesMockServer kubernetesMockServer;

	private KubernetesClient kubernetesClient;

	@Value("${k8s.target.namespace}")
    private String namespace;

	@Autowired
	private DeletePodScheduledJob deletePodScheduledJob;

	@Test
	void deleteRandomPodInTestNamespace() {
    // Given
	log.info("TEST");
    kubernetesMockServer.expect().get()
        .withPath("/api/v1/namespaces/" + namespace + "/pods?labelSelector=foo%3Dbar")
        .andReturn(HTTP_OK, new PodListBuilder().addToItems(
                new PodBuilder()
                    .withNewMetadata()
                    .withName("pod1")
                    .addToLabels("foo", "bar")
                    .endMetadata()
                    .build())
            .build())
        .once();

		log.info("POD OK");

		deletePodScheduledJob.deleteRandomPodFromNamespace();
	}

}
