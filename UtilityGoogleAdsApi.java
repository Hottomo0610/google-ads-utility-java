import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.lib.utils.FieldMasks;
import com.google.ads.googleads.v7.enums.GoogleAdsFieldDataTypeEnum;
import com.google.ads.googleads.v7.common.AdScheduleInfo;
import com.google.ads.googleads.v7.common.DeviceInfo;
import com.google.ads.googleads.v7.common.ExpandedTextAdInfo;
import com.google.ads.googleads.v7.common.ExpandedTextAdInfo.Builder;
import com.google.ads.googleads.v7.common.IpBlockInfo;
import com.google.ads.googleads.v7.common.Keyword;
import com.google.ads.googleads.v7.common.KeywordInfo;
import com.google.ads.googleads.v7.common.LanguageInfo;
import com.google.ads.googleads.v7.common.LocationInfo;
import com.google.ads.googleads.v7.common.ManualCpc;
import com.google.ads.googleads.v7.common.PolicyTopicEntry;
import com.google.ads.googleads.v7.enums.AdCustomizerPlaceholderFieldEnum.AdCustomizerPlaceholderField;
import com.google.ads.googleads.v7.enums.AdGroupAdStatusEnum.AdGroupAdStatus;
import com.google.ads.googleads.v7.enums.AdGroupCriterionStatusEnum.AdGroupCriterionStatus;
import com.google.ads.googleads.v7.enums.AdGroupStatusEnum.AdGroupStatus;
import com.google.ads.googleads.v7.enums.AdGroupTypeEnum.AdGroupType;
import com.google.ads.googleads.v7.enums.AdvertisingChannelTypeEnum.AdvertisingChannelType;
import com.google.ads.googleads.v7.enums.BatchJobStatusEnum.BatchJobStatus;
import com.google.ads.googleads.v7.enums.BiddingStrategyTypeEnum.BiddingStrategyType;
import com.google.ads.googleads.v7.enums.BudgetDeliveryMethodEnum.BudgetDeliveryMethod;
import com.google.ads.googleads.v7.enums.CampaignServingStatusEnum.CampaignServingStatus;
import com.google.ads.googleads.v7.enums.CampaignStatusEnum.CampaignStatus;
import com.google.ads.googleads.v7.enums.DayOfWeekEnum.DayOfWeek;
import com.google.ads.googleads.v7.enums.DeviceEnum.Device;
import com.google.ads.googleads.v7.enums.FeedAttributeTypeEnum.FeedAttributeType;
import com.google.ads.googleads.v7.enums.FeedItemStatusEnum.FeedItemStatus;
import com.google.ads.googleads.v7.enums.FeedItemTargetTypeEnum.FeedItemTargetType;
import com.google.ads.googleads.v7.enums.KeywordMatchTypeEnum.KeywordMatchType;
import com.google.ads.googleads.v7.enums.MinuteOfHourEnum.MinuteOfHour;
import com.google.ads.googleads.v7.enums.PlaceholderTypeEnum.PlaceholderType;
import com.google.ads.googleads.v7.enums.PolicyApprovalStatusEnum.PolicyApprovalStatus;
import com.google.ads.googleads.v7.errors.ErrorCode.ErrorCodeCase;
import com.google.ads.googleads.v7.errors.GoogleAdsError;
import com.google.ads.googleads.v7.errors.GoogleAdsException;
import com.google.ads.googleads.v7.errors.PolicyFindingDetails;
import com.google.ads.googleads.v7.errors.PolicyFindingErrorEnum.PolicyFindingError;
import com.google.ads.googleads.v7.resources.Ad;
import com.google.ads.googleads.v7.resources.AdGroup;
import com.google.ads.googleads.v7.resources.AdGroupAd;
import com.google.ads.googleads.v7.resources.AdGroupCriterion;
import com.google.ads.googleads.v7.resources.AttributeFieldMapping;
import com.google.ads.googleads.v7.resources.BatchJob;
import com.google.ads.googleads.v7.resources.Campaign;
import com.google.ads.googleads.v7.resources.CampaignBudget;
import com.google.ads.googleads.v7.resources.CampaignCriterion;
import com.google.ads.googleads.v7.resources.CampaignLabel;
import com.google.ads.googleads.v7.resources.CampaignSharedSet;
import com.google.ads.googleads.v7.resources.Feed;
import com.google.ads.googleads.v7.resources.FeedAttribute;
import com.google.ads.googleads.v7.resources.FeedItem;
import com.google.ads.googleads.v7.resources.FeedItemAttributeValue;
import com.google.ads.googleads.v7.resources.FeedItemTarget;
import com.google.ads.googleads.v7.resources.FeedMapping;
import com.google.ads.googleads.v7.resources.SharedSet;
import com.google.ads.googleads.v7.services.AdGroupAdOperation;
import com.google.ads.googleads.v7.services.AdGroupAdServiceClient;
import com.google.ads.googleads.v7.services.AdGroupCriterionOperation;
import com.google.ads.googleads.v7.services.AdGroupCriterionServiceClient;
import com.google.ads.googleads.v7.services.AdGroupOperation;
import com.google.ads.googleads.v7.services.AdGroupServiceClient;
import com.google.ads.googleads.v7.services.AddBatchJobOperationsRequest;
import com.google.ads.googleads.v7.services.AddBatchJobOperationsResponse;
import com.google.ads.googleads.v7.services.BatchJobOperation;
import com.google.ads.googleads.v7.services.BatchJobResult;
import com.google.ads.googleads.v7.services.BatchJobServiceClient;
import com.google.ads.googleads.v7.services.BatchJobServiceClient.ListBatchJobResultsPagedResponse;
import com.google.ads.googleads.v7.services.CampaignBudgetOperation;
import com.google.ads.googleads.v7.services.CampaignBudgetServiceClient;
import com.google.ads.googleads.v7.services.CampaignCriterionOperation;
import com.google.ads.googleads.v7.services.CampaignCriterionServiceClient;
import com.google.ads.googleads.v7.services.CampaignLabelOperation;
import com.google.ads.googleads.v7.services.CampaignLabelServiceClient;
import com.google.ads.googleads.v7.services.CampaignOperation;
import com.google.ads.googleads.v7.services.CampaignServiceClient;
import com.google.ads.googleads.v7.services.CampaignSharedSetOperation;
import com.google.ads.googleads.v7.services.FeedItemOperation;
import com.google.ads.googleads.v7.services.FeedItemServiceClient;
import com.google.ads.googleads.v7.services.FeedItemTargetOperation;
import com.google.ads.googleads.v7.services.FeedItemTargetServiceClient;
import com.google.ads.googleads.v7.services.FeedMappingOperation;
import com.google.ads.googleads.v7.services.FeedMappingServiceClient;
import com.google.ads.googleads.v7.services.FeedOperation;
import com.google.ads.googleads.v7.services.FeedServiceClient;
import com.google.ads.googleads.v7.services.GoogleAdsRow;
import com.google.ads.googleads.v7.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v7.services.GoogleAdsServiceClient.SearchPagedResponse;
import com.google.ads.googleads.v7.services.GoogleAdsServiceSettings;
import com.google.ads.googleads.v7.services.ListBatchJobResultsRequest;
import com.google.ads.googleads.v7.services.MutateAdGroupAdsResponse;
import com.google.ads.googleads.v7.services.MutateAdGroupCriteriaResponse;
import com.google.ads.googleads.v7.services.MutateAdGroupCriterionResult;
import com.google.ads.googleads.v7.services.MutateAdGroupResult;
import com.google.ads.googleads.v7.services.MutateAdGroupsResponse;
import com.google.ads.googleads.v7.services.MutateCampaignBudgetsResponse;
import com.google.ads.googleads.v7.services.MutateCampaignCriteriaResponse;
import com.google.ads.googleads.v7.services.MutateCampaignCriterionResult;
import com.google.ads.googleads.v7.services.MutateCampaignLabelResult;
import com.google.ads.googleads.v7.services.MutateCampaignLabelsResponse;
import com.google.ads.googleads.v7.services.MutateCampaignResult;
import com.google.ads.googleads.v7.services.MutateCampaignsResponse;
import com.google.ads.googleads.v7.services.MutateFeedItemResult;
import com.google.ads.googleads.v7.services.MutateFeedItemTargetsResponse;
import com.google.ads.googleads.v7.services.MutateFeedItemsResponse;
import com.google.ads.googleads.v7.services.MutateFeedMappingsResponse;
import com.google.ads.googleads.v7.services.MutateFeedsResponse;
import com.google.ads.googleads.v7.services.MutateOperation;
import com.google.ads.googleads.v7.services.MutateOperationResponse;
import com.google.ads.googleads.v7.services.MutateOperationResponse.ResponseCase;
import com.google.ads.googleads.v7.services.SearchGoogleAdsRequest;
import com.google.ads.googleads.v7.services.SearchGoogleAdsStreamRequest;
import com.google.ads.googleads.v7.services.SearchGoogleAdsStreamResponse;
import com.google.ads.googleads.v7.utils.ResourceNames;
import com.google.api.ads.common.lib.conf.ConfigurationLoadException;
import com.google.api.ads.common.lib.exception.OAuthException;
import com.google.api.ads.common.lib.exception.ValidationException;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.rpc.ServerStream;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Singleton;
import com.google.rpc.Status;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilityGoogleAdsApi {
    private static final Logger logger = LoggerFactory.getLogger(AdWordsCampaignManager.class);

    /** ステータス：有効／アクティブ */
	private static final int SET_STATUS_ACTIVE = 0;
	/** ステータス：一時停止 */
	private static final int SET_STATUS_PAUSED = 1;
	/** ステータス：削除／無効 */
	private static final int SET_STATUS_DELETED = 2;

	private List<String> adWordsClientIdList;

    private static String localClientId = ""; 

	private static long temporaryId = -1l;

    /**
	 * コンストラクタ
	 */
	public UtilityGoogleAdsApi() {
		logger.debug("GOOGLE CONSTRUCTOR");
		adWordsClientIdList = new ArrayList<String>();

		// 以下、DBからアカウントIDを取得し、リストに格納する処理を追加
        // todo
	}


    /**
	 * 
	 * キャンペーンを取得して、それをListに格納して返します。 
	 *
	 * @return the campaigns list
	 */
	private List<Campaign> getCampaignList(GoogleAdsClient googleAdsClient, String clientId, List<Long> campaignIds)
            throws Exception {
        List<Campaign> campaignList = new ArrayList<Campaign>();
        String query = "";
        if (campaignIds.isEmpty()){
            query = "SELECT campaign.id, campaign.name, campaign.status, campaign.serving_status, campaign.start_date FROM campaign ORDER BY campaign.name ASC";
        }
        else {
            query = String.format(
                "SELECT campaign.id, campaign.name, campaign.status, campaign.serving_status, campaign.start_date FROM campaign WHERE campaign.id IN (%s)", 
                StringUtils.join(campaignIds, ", ")); 
        }
        
        try (GoogleAdsServiceClient googleAdsServiceClient = 
                 googleAdsClient.getLatestVersion().createGoogleAdsServiceClient()) {
            SearchGoogleAdsStreamRequest request = SearchGoogleAdsStreamRequest.newBuilder()
                .setCustomerId(clientId)
                   .setQuery(query)
                .build();
        
            ServerStream<SearchGoogleAdsStreamResponse> stream =
                googleAdsServiceClient.searchStreamCallable().call(request);
        
            for (SearchGoogleAdsStreamResponse response : stream) {
                for (GoogleAdsRow googleAdsRow : response.getResultsList()) {
                    campaignList.add(googleAdsRow.getCampaign());
                }
            }
        } catch(GoogleAdsException e) {
            logger.debug("該当キャンペーンが存在しないアカウントでSearchしました。");
        }
        
        return campaignList;
    }



    /**
	 * キャンペーンを取得します。
	 * API移行済みです。
	 * 
	 * @param campaignName
	 * @return
	 */
	private Campaign getCampaign(String customerId, long campaignId) throws Exception {
		GoogleAdsClient googleAdsClient = initGoogleAdsClient();
		GoogleAdsServiceClient googleAdsServiceClient = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient();
		//String query = String.format("SELECT campaign.id, campaign.name WHERE campaign.id = '%d'", searchSubField.campaignId);
		String query = String.format("SELECT campaign.id, campaign.name, campaign.resource_name, campaign.status FROM campaign WHERE campaign.status != 'REMOVED' AND campaign.resource_name = '%d'", campaignId);
		SearchGoogleAdsStreamRequest request = SearchGoogleAdsStreamRequest.newBuilder()
			.setCustomerId(customerId)
			.setQuery(query)
			.build();
				
		ServerStream<SearchGoogleAdsStreamResponse> stream = googleAdsServiceClient.searchStreamCallable().call(request);

		Campaign campaign = null;

		for (SearchGoogleAdsStreamResponse response : stream){
			for (GoogleAdsRow googleAdsRow : response.getResultsList()){
				campaign = googleAdsRow.getCampaign();
			}
		}		
		return campaign;
	}



	/**
	 * キャンペーンを取得します。
	 * 
	 * @param campaignName
	 * @return
	 */
	public Campaign getCampaignByName(String customerId, String campaignName) throws Exception {
		GoogleAdsClient googleAdsClient = initGoogleAdsClient();
		GoogleAdsServiceClient googleAdsServiceClient = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient();
		Campaign campaign = null;
		try {	
			String query = String.format("SELECT campaign.id, campaign.name, campaign.resource_name, campaign.status FROM campaign WHERE campaign.status != 'REMOVED' AND campaign.name = '%s'", campaignName);
	
			SearchGoogleAdsStreamRequest request =
				SearchGoogleAdsStreamRequest.newBuilder()
					.setCustomerId(customerId)
					.setQuery(query)
					.build();
			
			ServerStream<SearchGoogleAdsStreamResponse> stream =
				googleAdsServiceClient.searchStreamCallable().call(request);
			
			for (SearchGoogleAdsStreamResponse response : stream){
				for (GoogleAdsRow googleAdsRow : response.getResultsList()){
					campaign = googleAdsRow.getCampaign();
				}
			}
		}catch (Exception e){
			logger.debug("このアカウントには該当するキャンペーンは存在しません。");
		}
		return campaign;
	}



	/**
	 * キャンペーンを取得します。
	 * 
	 * @param campaignResourceName
	 * @return 
	 */
	public Campaign getCampaignByResourceName(String customerId, String campaignResourceName) throws Exception {
		GoogleAdsClient googleAdsClient = initGoogleAdsClient();
		GoogleAdsServiceClient googleAdsServiceClient = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient();
		Campaign campaign = null;
		try{
			String query = String.format("SELECT campaign.id, campaign.name, campaign.resource_name, campaign.status FROM campaign WHERE campaign.resource_name = '%s'", campaignResourceName);
	
			SearchGoogleAdsStreamRequest request =
				SearchGoogleAdsStreamRequest.newBuilder()
					.setCustomerId(customerId)
					.setQuery(query)
					.build();
			
			ServerStream<SearchGoogleAdsStreamResponse> stream =
				googleAdsServiceClient.searchStreamCallable().call(request);
			
			for (SearchGoogleAdsStreamResponse response : stream){
				for (GoogleAdsRow googleAdsRow : response.getResultsList()){
					campaign = googleAdsRow.getCampaign();
				}
			}
		}catch (Exception e){
			logger.debug("このアカウントには該当するキャンペーンは存在しません。");
		}
		return campaign;
	}



    /**
     * 特定のキャンペーン内の広告グループを取得する
     * 
     * @param customerId
     * @param campaignResourceName
     * @return
     * @throws Exception
     */
    public List<AdGroup> getAdGroupInCampaign(String customerId, String campaignRN) throws Exception {
        GoogleAdsClient googleAdsClient = initGoogleAdsClient();
        GoogleAdsServiceClient googleAdsServiceClient = 
            googleAdsClient.getLatestVersion().createGoogleAdsServiceClient();
        List<AdGroup> adGroupList = new ArrayList<>();

        String query = String.format(
			"SELECT ad_group.id, ad_group.name, ad_group.status, ad_group.resource_name, campaign.name FROM ad_group WHERE campaign.resource_name = '%s'",
			campaignRN);

        SearchGoogleAdsRequest request =
		  SearchGoogleAdsRequest.newBuilder()
			  .setCustomerId(customerId)
			  .setPageSize(1000)
			  .setQuery(query)
			  .build();

		SearchPagedResponse searchPagedResponse = googleAdsServiceClient.search(request);

        for (GoogleAdsRow googleAdsRow : searchPagedResponse.iterateAll()) {
			adGroupList.add(googleAdsRow.getAdGroup());
		}
        return adGroupList;
    }



    /**
     * 特定のキャンペーン内で、特定の文字列を名前に含む広告グループを取得する
     * 
     * @param customerId
     * @param campaignResourceName
     * @param adGroupName
     * @return
     * @throws Exception
     */
    public List<AdGroup> getAdGroupByNameInCampaign(String customerId, String campaignRN, String adGroupStr) throws Exception {
        GoogleAdsClient googleAdsClient = initGoogleAdsClient();
        GoogleAdsServiceClient googleAdsServiceClient = 
            googleAdsClient.getLatestVersion().createGoogleAdsServiceClient();
        List<AdGroup> adGroupList = new ArrayList<>();

        String query = String.format(
			"SELECT ad_group.id, ad_group.name, ad_group.status, ad_group.resource_name, campaign.name FROM ad_group WHERE campaign.resource_name = '%s' AND ad_group.name = '%s'",
			campaignRN, adGroupStr);

        SearchGoogleAdsRequest request =
		  SearchGoogleAdsRequest.newBuilder()
			  .setCustomerId(customerId)
			  .setPageSize(1000)
			  .setQuery(query)
			  .build();

		SearchPagedResponse searchPagedResponse = googleAdsServiceClient.search(request);

        for (GoogleAdsRow googleAdsRow : searchPagedResponse.iterateAll()) {
			adGroupList.add(googleAdsRow.getAdGroup());
		}
        return adGroupList;
    }




    /**
	 * バッチ処理の返答を得ます
	 * API移行済みです。
	 * 
	 * @param batchJobServiceClient
	 * @param batchJobResourceName
	 * @param operations
	 * @return
	 * @throws Exception
	 */
	public ListBatchJobResultsPagedResponse getBatchJobResponse(
            BatchJobServiceClient batchJobServiceClient, String clientId,
            String batchJobResourceName, OperationFuture operationResponse) throws Exception {
    
        pollBatchJob(batchJobServiceClient, clientId, operationResponse, batchJobResourceName);
    
        //logger.info("AdGroupAd policy exemption requests were sended.");
        ListBatchJobResultsPagedResponse batchJobResults =
            batchJobServiceClient.listBatchJobResults(
                ListBatchJobResultsRequest.newBuilder()
                    .setResourceName(batchJobResourceName)
                    .setPageSize(1000)
                    .build());
    
        return batchJobResults;
    }



    /**
	 * バッチジョブが完了するまで、バッチジョブのステータスをチェックし続けます。
	 * API移行済みです。
	 * 
	 * @param batchJobServiceClient
	 * @param clientId
	 * @param operationResponse
	 * @return
	 * @throws Exception
	 */
	private void pollBatchJob(BatchJobServiceClient batchJobServiceClient, String clientId, 
        OperationFuture operationResponse, String batchJobResourceName) throws Exception {
    int i = 0;
    int waitTime = 60;
    do {
        try {
            //バッチジョブが完了したか定期的にチェックします。
            operationResponse.get(waitTime, TimeUnit.SECONDS);
            waitTime = waitTime + 60; 
            i++;
          } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.err.printf("Failed polling the mutate job. Exception: %s%n", e);
            //APIの通信結果でエラーをキャッチした場合
          } catch (GoogleAdsException gae) {
            List<GoogleAdsError> errors = gae.getGoogleAdsFailure().getErrorsList();
    
            for(GoogleAdsError error : errors) {
                //ポリシー違反でエラーが出た場合、オペレーションを再申請します。
                PolicyFindingError policy = error.getErrorCode().getPolicyFindingError();
    
                if(policy == PolicyFindingError.POLICY_FINDING) {
                    ListBatchJobResultsPagedResponse batchJobResults =
                        batchJobServiceClient.listBatchJobResults(
                            ListBatchJobResultsRequest.newBuilder()
                                .setResourceName(batchJobResourceName)
                                .build());
    
                    for (BatchJobResult batchJobResult : batchJobResults.iterateAll()) {
                        AdGroupAd adGroupAd = batchJobResult.getMutateOperationResponse().getAdGroupAdResult().getAdGroupAd();
                        PolicyApprovalStatus policyStatus = adGroupAd.getPolicySummary().getApprovalStatus();
    
                        if (policyStatus == PolicyApprovalStatus.DISAPPROVED) {
                            List<String> ignorablePolicyTopics = fetchIgnorablePolicyTopics(gae);
    
                            AdGroupAd newAd = adGroupAd.toBuilder().build();
                            AdGroupAdOperation operation = 
                                AdGroupAdOperation.newBuilder()
                                    .setCreate(newAd)
                                    .build();
    
                            requestExemption(ignorablePolicyTopics, operation, clientId);
                        }
                    }
                }
            }
        }
    } while(i<5);
    //return "BatchJob is done.";
    }


    
    /**
    * ポリシーエラーで完了できなかったオペレーションを再申請するときに送信される、無視できるポリシートピックを収集します。
    * API移行済みです。
    * 
    * @param gae
    * @return
    */
    private List<String> fetchIgnorablePolicyTopics(GoogleAdsException gae) {
    System.out.println("Google Ads failure details:");
    
    // ポリシートピックを入れるリストを作成します。
    List<String> ignorableTopics = new ArrayList<>();
    
    // 無視できるポリシーエラーに関するエラーをサーチします。
    for (GoogleAdsError error : gae.getGoogleAdsFailure().getErrorsList()) {
      // Supports sending exemption request for the policy finding error only.
      if (error.getErrorCode().getErrorCodeCase() != ErrorCodeCase.POLICY_FINDING_ERROR) {
        throw gae;
      }
    
      // Shows some information about the error encountered.
      System.out.printf("\t%s: %s%n", error.getErrorCode().getErrorCodeCase(), error.getMessage());
    
      if (error.getDetails() != null) {
        PolicyFindingDetails policyFindingDetails = error.getDetails().getPolicyFindingDetails();
        if (policyFindingDetails != null) {
          System.out.println("\tPolicy finding details:");
    
          for (PolicyTopicEntry policyTopicEntry : policyFindingDetails.getPolicyTopicEntriesList()) {
            ignorableTopics.add(policyTopicEntry.getTopic());
            System.out.printf("\t\tPolicy topic name: '%s'%n", policyTopicEntry.getTopic());
            System.out.printf("\t\tPolicy topic entry type: '%s'%n", policyTopicEntry.getType());
          }
        }
      }
    }
    return ignorableTopics;
    }


    
    /**
    * ポリシーエラーで完了できなかった広告追加オペレーションの再申請を行います。
    * API移行済みです。
    *
    * @param ignorablePolicyTopics 
    * @param operation
    * @param clientId
    * @throws Exception
    */
    public MutateAdGroupAdsResponse requestExemption(
        List<String> ignorablePolicyTopics, AdGroupAdOperation operation, String clientId) throws Exception {
    
    GoogleAdsClient googleAdsClient = initGoogleAdsClient();
    AdGroupAdServiceClient client =
        googleAdsClient.getLatestVersion().createAdGroupAdServiceClient();
    
    //再申請用のオペレーションのビルダーを作成します。
    AdGroupAdOperation.Builder operationBuilder = operation.toBuilder();
    
    //エラー回避のリクエストを追加します。
    operationBuilder
        .getPolicyValidationParameterBuilder()
        .addAllIgnorablePolicyTopics(ignorablePolicyTopics);
    
    //リクエストを再送信します。
    MutateAdGroupAdsResponse response =
        client.mutateAdGroupAds(clientId, ImmutableList.of(operationBuilder.build()));
    
    return response;
    }




    /**
	 * AdWordsのセッションを初期化して返します。
	 * API移行済みです。
	 * 
	 * @param AdWords ClientCustomerId
	 * @return
	 * @throws OAuthException
	 * @throws ValidationException
	 * @throws ConfigurationLoadException
	 * @throws IOException
	 */
	private GoogleAdsClient initGoogleAdsClient() throws OAuthException, ValidationException, ConfigurationLoadException, IOException {
		//config = ConfigFactory.load();
		// Generate a refreshable OAuth2 credential.
		File propFile = new File("propertiesFile path");

		GoogleAdsClient googleAdsClient = null;

		googleAdsClient = GoogleAdsClient.newBuilder().fromPropertiesFile(propFile).build();

		return googleAdsClient;
	}



    /**
	 * Returns the next temporary ID and decreases it by one.
	 *
	 * @return the next temporary ID.
	 */
	private long getNextTemporaryId() {
        return temporaryId--;
    }
}
