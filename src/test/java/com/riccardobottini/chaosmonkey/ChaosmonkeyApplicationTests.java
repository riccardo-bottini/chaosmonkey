package com.riccardobottini.chaosmonkey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;

@SpringBootTest
@EnableKubernetesMockClient(crud = true)
class ChaosmonkeyApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(ChaosmonkeyApplicationTests.class);

    KubernetesMockServer server;

    private KubernetesClient client;

    @Value("${k8s.target.namespace}")
    private String namespace;

    @Autowired
    private DeletePodScheduledJob deletePodScheduledJob;

    @Test
    void deletePodsInNamespace() {

        log.info("deletePodsInNamespace {} -  start", namespace);

        // Build and create pods
        Pod pod1 = new PodBuilder().withNewMetadata().withName("pod1").endMetadata().build();
        Pod pod2 = new PodBuilder().withNewMetadata().withName("pod2").endMetadata().build();

        client.pods().inNamespace(namespace).resource(pod1).create();
        client.pods().inNamespace(namespace).resource(pod2).create();

        // Check size
        PodList podList = client.pods().inNamespace(namespace).list();
        assertEquals(2, podList.getItems().size());

        // Test the deletion
        assertEquals(1, deletePodScheduledJob.deleteRandomPodFromNamespace(client));
    }
}
