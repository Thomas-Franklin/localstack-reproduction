package not.working.as.expected;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.amazonaws.services.sqs.buffered.QueueBufferConfig;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;

import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SenderLambda implements RequestHandler<LinkedHashMap, String> {

    private static final Logger logger = LoggerFactory.getLogger(SenderLambda.class);

    public String handleRequest(final LinkedHashMap request, final Context context) {
        
        AmazonSQSAsync amazonSQSAsync = provideSqsClient();

        amazonSQSAsync.sendMessage(new SendMessageRequest(System.getenv("QUEUE_URL"), "foo"));
        
        LambdaResponse response = new LambdaResponse();
        
        response.setStatus(200);

        return new Gson().toJson(response);
    }

    private AmazonSQSBufferedAsyncClient provideSqsClient() {

        QueueBufferConfig queueBuffer = new QueueBufferConfig().withMaxInflightOutboundBatches(10);

        return new AmazonSQSBufferedAsyncClient(getClient(), queueBuffer);
    }

    private AmazonSQSAsync getClient() {
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(System.getenv("OVERRIDE_QUEUE_URL"), System.getenv("AWS_REGION"));
        return AmazonSQSAsyncClientBuilder.standard().withEndpointConfiguration(endpointConfiguration).build();
    }
}
