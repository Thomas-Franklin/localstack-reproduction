package not.working.as.expected;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceivingLambda implements RequestHandler<SQSEvent, String> {

    private static final Logger logger = LoggerFactory.getLogger(ReceivingLambda.class);

    public String handleRequest(final SQSEvent request, final Context context) {
        
        AmazonSQSAsync amazonSQSAsync = getClient();

        for (SQSMessage message : request.getRecords()) {
            amazonSQSAsync.deleteMessage(new DeleteMessageRequest(System.getenv("QUEUE_URL"), message.getReceiptHandle()));
        }

        LambdaResponse response = new LambdaResponse();
        
        response.setStatus(200);

        return new Gson().toJson(response);
    }

    private AmazonSQSAsync getClient() {
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(System.getenv("OVERRIDE_QUEUE_URL"), System.getenv("AWS_REGION"));
        return AmazonSQSAsyncClientBuilder.standard().withEndpointConfiguration(endpointConfiguration).build();
    }
}
