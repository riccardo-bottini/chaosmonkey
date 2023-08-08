package com.riccardobottini.chaosmonkey;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;

@Component
public class DeletePodScheduledJob {

    @Value("${k8s.target.namespace}")
    private String namespace;

    private Random random = new Random();

    private static final Logger log = LoggerFactory.getLogger(DeletePodScheduledJob.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRateString = "${scheduled.fixedrate.milliseconds}")
    public void deleteRandomPodFromNamespace() {
        log.info("Checking a random pod to be deleted at {}", dateFormat.format(new Date()));

        ApiClient apiClient;
        try {
            apiClient = Config.defaultClient();
            Configuration.setDefaultApiClient(apiClient);
            CoreV1Api coreV1Api = new CoreV1Api(apiClient);

            log.info("Getting pods from {}", namespace);

            V1PodList podList = coreV1Api.listNamespacedPod(
                namespace, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null,
                null, 
                null
            );

            for (V1Pod pod : podList.getItems()){
                log.info("Pod name: {}", pod.getMetadata().getName());
            }
            
            int randomListElementIndex = random.nextInt(podList.getItems().size());

            V1Pod podToBeDeleted = podList.getItems().get(randomListElementIndex);
            V1ObjectMeta podMetadata = podToBeDeleted.getMetadata();

            if (podMetadata != null) {
                V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();

                v1DeleteOptions.setApiVersion(podToBeDeleted.getApiVersion());
                
                V1Pod v1Status = coreV1Api.deleteNamespacedPod(
                    podMetadata.getName(),
                    namespace,
                    null,
                    null,
                    null,
                    null,
                    null,
                    v1DeleteOptions
                );

                log.info("Status: {}", v1Status.getStatus());
            } else {
                log.warn("Pod Metadata is null!");
            }

        } catch (IOException e) {
            log.error("Error with ApiClient!", e);
            e.printStackTrace();
        } catch (ApiException e) {
            log.error("Error in get pod from namespace {} ", namespace);
            log.info(e.getResponseBody());
            //e.printStackTrace();
        }
    }
}
