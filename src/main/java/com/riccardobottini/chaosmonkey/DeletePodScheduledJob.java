package com.riccardobottini.chaosmonkey;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

@Component
public class DeletePodScheduledJob {

    @Value("${k8s.target.namespace}")
    private String namespace;

    @Value("${k8s.target.label.key}")
    private String labelKey;

    @Value("${k8s.target.label.value}")
    private String labelValue;

    private Random random = new Random();

    private static final Logger log = LoggerFactory.getLogger(DeletePodScheduledJob.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRateString = "${scheduler.fixedrate.milliseconds}", initialDelayString = "${scheduler.initialdelay.milliseconds}")
    public void deleteRandomPodFromNamespace() {

        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            this.deleteRandomPodFromNamespace(client, labelKey, labelValue);
        }
    }

    public int deleteRandomPodFromNamespace(KubernetesClient client, String labelKey, String labelValue) {

        log.info("Checking a random pod to be deleted at {}", dateFormat.format(new Date()));

        int resultSize = 0;

        try {
            log.info("labelKey: {}, labelValue: {}", labelKey, labelValue);
            PodList podList;
            if (!labelKey.isBlank() && !labelValue.isBlank()) {
                podList = client.pods()
                        .inNamespace(namespace)
                        .withLabels(Collections.singletonMap(labelKey, labelValue))
                        .list();
            } else {
                // List pod in namespace
                podList = client.pods().inNamespace(namespace).list();
            }

            if (!podList.getItems().isEmpty()) {
                // Get number of pods
                resultSize = podList.getItems().size();
                log.info("Pod List size: {}", resultSize);

                // Get the pod that will be deleted
                Pod podToBeDeleted = podList.getItems().get(random.nextInt(resultSize));
                String podName = podToBeDeleted.getMetadata().getName();
                log.info("Pod name: {}", podName);

                // Delete pod
                client.pods().inNamespace(namespace).withName(podName).delete();

                // Check if the pod is actually deleted
                podList = client.pods().inNamespace(namespace).list();
                resultSize = podList.getItems().size();

                if (!podList.getItems().contains(podToBeDeleted)) {
                    log.info("{} deleted from namespace {}!", podName, namespace);
                } else {
                    log.error("{} not deleted from namespace {}!", podName, namespace);
                }
            }
        } finally {
            client.close();
        }
        return resultSize;
    }
}
