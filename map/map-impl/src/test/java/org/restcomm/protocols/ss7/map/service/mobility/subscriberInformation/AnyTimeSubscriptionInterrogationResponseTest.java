package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.restcomm.protocols.ss7.commonapp.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.commonapp.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.commonapp.api.subscriberManagement.BearerServiceCodeValue;
import org.restcomm.protocols.ss7.commonapp.api.subscriberManagement.ExtBasicServiceCode;
import org.restcomm.protocols.ss7.commonapp.api.subscriberManagement.TeleserviceCodeValue;
import org.restcomm.protocols.ss7.commonapp.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.commonapp.primitives.MAPExtensionContainerTest;
import org.restcomm.protocols.ss7.commonapp.subscriberInformation.CSGIdImpl;
import org.restcomm.protocols.ss7.commonapp.subscriberManagement.ExtBasicServiceCodeImpl;
import org.restcomm.protocols.ss7.commonapp.subscriberManagement.ExtBearerServiceCodeImpl;
import org.restcomm.protocols.ss7.commonapp.subscriberManagement.ExtTeleserviceCodeImpl;
import org.restcomm.protocols.ss7.commonapp.subscriberManagement.SupportedCamelPhasesImpl;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.CAMELSubscriptionInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.CallBarringData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.CallForwardingData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ExtCwFeature;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MSISDNBS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ODBInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CSGSubscriptionData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DefaultCallHandling;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtCallBarringFeature;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtForwFeature;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OBcsmCamelTDPData;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.OBcsmTriggerDetectionPoint;
import org.restcomm.protocols.ss7.map.api.service.supplementary.CliRestrictionOption;
import org.restcomm.protocols.ss7.map.api.service.supplementary.OverrideCategory;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.CSGSubscriptionDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtCallBarringFeatureImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtForwFeatureImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ExtSSStatusImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.OBcsmCamelTDPDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.OCSIImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ODBDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ODBGeneralDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.ODBHPLMNDataImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.OfferedCamel4CSIsImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.PasswordImpl;
import org.testng.annotations.Test;

import com.mobius.software.telco.protocols.ss7.asn.ASNDecodeResult;
import com.mobius.software.telco.protocols.ss7.asn.ASNParser;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author vadim subbotin
 */
public class AnyTimeSubscriptionInterrogationResponseTest {
    private byte[] data = { 48, -127, -42, -95, 13, 48, 11, 48, 9, -126, 1, 0, -124, 1, 15, -121, 1, 10, -94, 21, 48, 8, 48, 6, -126, 1, 96, -124, 1, 8, 18, 4, 48, 48, 48, 48, 2, 1, 3, 5, 0, -93, 13, 48, 9, 3, 3, 2, -1, -4, 3, 2, 4, -16, 5, 0, -92, 32, -96, 26, 48, 19, 48, 17, 10, 1, 2, 2, 1, 20, -128, 6, -111, 33, 67, 101, -121, 9, -127, 1, 0, -128, 1, 5, -126, 0, -121, 0, -120, 0, -123, 2, 4, -16, -122, 2, 6, -64, -89, 39, -96, 32, 48, 10, 6, 3, 42, 3, 4, 11, 12, 13, 14, 15, 48, 5, 6, 3, 42, 3, 6, 48, 11, 6, 3, 42, 3, 5, 21, 22, 23, 24, 25, 26, -95, 3, 31, 32, 33, -120, 2, 1, -2, -119, 2, 4, -16, -86, 16, 48, 14, 4, 7, -111, -105, 97, 33, 67, 101, -9, -96, 3, -125, 1, 0, -85, 6, 48, 4, 3, 2, 0, 0, -84, 12, -95, 10, 48, 8, -95, 3, -126, 1, 96, -126, 1, 8, -83, 5, -127, 1, 4, -126, 0, -82, 6, -127, 1, 4, -126, 1, 0, -81, 8, -127, 1, 4, -126, 1, 0, -125, 0, -80, 3, -127, 1, 4 };

    @Test(groups = { "functional.decode", "subscriberInformation" })
    public void testDecode() throws Exception {
    	ASNParser parser=new ASNParser();
    	parser.replaceClass(AnyTimeSubscriptionInterrogationResponseImpl.class);
    	
    	ASNDecodeResult result=parser.decode(Unpooled.wrappedBuffer(data));
        assertFalse(result.getHadErrors());
        assertTrue(result.getResult() instanceof AnyTimeSubscriptionInterrogationResponseImpl);
        AnyTimeSubscriptionInterrogationResponseImpl response = (AnyTimeSubscriptionInterrogationResponseImpl)result.getResult();

        assertTrue(MAPExtensionContainerTest.CheckTestExtensionContainer(response.getExtensionContainer()));

        CallForwardingData callForwardingData = response.getCallForwardingData();
        assertNotNull(callForwardingData.getForwardingFeatureList());
        assertEquals(callForwardingData.getForwardingFeatureList().size(), 1);
        assertFalse(callForwardingData.getNotificationToCSE());

        ExtForwFeature extForwFeature = callForwardingData.getForwardingFeatureList().get(0);
        assertEquals(extForwFeature.getBasicService().getExtBearerService().getBearerServiceCodeValue(), BearerServiceCodeValue.allBearerServices);
        assertTrue(extForwFeature.getSsStatus().getBitQ());
        assertTrue(extForwFeature.getSsStatus().getBitP());
        assertTrue(extForwFeature.getSsStatus().getBitR());
        assertTrue(extForwFeature.getSsStatus().getBitA());
        assertEquals(extForwFeature.getNoReplyConditionTime().intValue(), 10);

        CallBarringData callBarringData = response.getCallBarringData();
        assertNotNull(callBarringData.getCallBarringFeatureList());
        assertEquals(callBarringData.getCallBarringFeatureList().size(), 1);
        assertEquals(callBarringData.getPassword().getData(), "0000");
        assertEquals(callBarringData.getWrongPasswordAttemptsCounter().intValue(), 3);
        assertTrue(callBarringData.getNotificationToCSE());

        ExtCallBarringFeature extCallBarringFeature = callBarringData.getCallBarringFeatureList().get(0);
        assertEquals(extCallBarringFeature.getBasicService().getExtBearerService().getBearerServiceCodeValue(), BearerServiceCodeValue.allAsynchronousServices);
        assertTrue(extCallBarringFeature.getSsStatus().getBitQ());
        assertFalse(extCallBarringFeature.getSsStatus().getBitP());
        assertFalse(extCallBarringFeature.getSsStatus().getBitR());
        assertFalse(extCallBarringFeature.getSsStatus().getBitA());

        ODBInfo odbInfo = response.getOdbInfo();
        assertNotNull(odbInfo.getOdbData());
        assertTrue(odbInfo.getNotificationToCSE());

        CAMELSubscriptionInfo camelSubscriptionInfo = response.getCamelSubscriptionInfo();
        assertNotNull(camelSubscriptionInfo.getOCsi());
        assertTrue(camelSubscriptionInfo.getTifCsi());
        assertTrue(camelSubscriptionInfo.getTifCsiNotificationToCSE());
        assertNotNull(camelSubscriptionInfo.getOCsi().getOBcsmCamelTDPDataList());
        assertEquals(camelSubscriptionInfo.getOCsi().getOBcsmCamelTDPDataList().size(), 1);

        OBcsmCamelTDPData oBcsmCamelTDPData = camelSubscriptionInfo.getOCsi().getOBcsmCamelTDPDataList().get(0);
        assertEquals(oBcsmCamelTDPData.getOBcsmTriggerDetectionPoint(), OBcsmTriggerDetectionPoint.collectedInfo);
        assertEquals(oBcsmCamelTDPData.getServiceKey(), 20);
        assertEquals(oBcsmCamelTDPData.getDefaultCallHandling(), DefaultCallHandling.continueCall);
        assertEquals(oBcsmCamelTDPData.getGsmSCFAddress().getAddress(), "1234567890");

        assertNotNull(response.getMsisdnBsList());
        assertEquals(response.getMsisdnBsList().size(), 1);
        MSISDNBS msisdnbs = response.getMsisdnBsList().get(0);
        assertEquals(msisdnbs.getMsisdn().getAddress(), "79161234567");
        assertNotNull(msisdnbs.getBasicServiceList());
        assertEquals(msisdnbs.getBasicServiceList().size(), 1);
        ExtBasicServiceCode basicServiceCode = msisdnbs.getBasicServiceList().get(0);
        assertEquals(basicServiceCode.getExtTeleservice().getTeleserviceCodeValue(), TeleserviceCodeValue.allTeleservices);

        assertNotNull(response.getCwData());
        assertNotNull(response.getChData());
        assertNotNull(response.getClipData());
        assertNotNull(response.getClirData());
        assertNotNull(response.getEctData());

        List<CSGSubscriptionData> csgSubscriptionDataList = response.getCsgSubscriptionDataList();
        assertNotNull(csgSubscriptionDataList.get(0).getCsgId());
    }

    @Test(groups = { "functional.encode", "subscriberInformation" })
    public void testEncode() throws Exception {
    	ASNParser parser=new ASNParser();
    	parser.replaceClass(AnyTimeSubscriptionInterrogationResponseImpl.class);
    	
        final ExtForwFeatureImpl extForwFeature = new ExtForwFeatureImpl(new ExtBasicServiceCodeImpl(
                new ExtBearerServiceCodeImpl(BearerServiceCodeValue.allBearerServices)),
                new ExtSSStatusImpl(true, true, true, true), null, null, null, 10, null, null);
        List<ExtForwFeature> extForwFeatureList=new ArrayList<ExtForwFeature>();
        extForwFeatureList.add(extForwFeature);
        CallForwardingDataImpl callForwardingData = new CallForwardingDataImpl(extForwFeatureList, false, null);

        final ExtCallBarringFeatureImpl extCallBarringFeature = new ExtCallBarringFeatureImpl(new ExtBasicServiceCodeImpl(
                new ExtBearerServiceCodeImpl(BearerServiceCodeValue.allAsynchronousServices)), new ExtSSStatusImpl(true, false, false, false), null);
        
        List<ExtCallBarringFeature> extCallBarringFeatureList=new ArrayList<ExtCallBarringFeature>();
        extCallBarringFeatureList.add(extCallBarringFeature);
        CallBarringDataImpl callBarringData = new CallBarringDataImpl(extCallBarringFeatureList,new PasswordImpl("0000"), 3, true, null);

        ODBDataImpl odbData = new ODBDataImpl(new ODBGeneralDataImpl(true, true, true, true, true, true, true, true, true, true, true, true, true,
                true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false),
                new ODBHPLMNDataImpl(true, true, true, true), null);
        ODBInfoImpl odbInfo = new ODBInfoImpl(odbData, true, null);

        ISDNAddressStringImpl gsmSCFAddress = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "1234567890");
        final OBcsmCamelTDPDataImpl oBcsmCamelTDPData = new OBcsmCamelTDPDataImpl(OBcsmTriggerDetectionPoint.collectedInfo, 20, gsmSCFAddress,
                DefaultCallHandling.continueCall, null);
        
        List<OBcsmCamelTDPData> oBcsmCamelTDPDataList=new ArrayList<OBcsmCamelTDPData>();
        oBcsmCamelTDPDataList.add(oBcsmCamelTDPData);
        OCSIImpl ocsi = new OCSIImpl(oBcsmCamelTDPDataList, null, 5, false, true);
        CAMELSubscriptionInfoImpl camelSubscriptionInfo = new CAMELSubscriptionInfoImpl(ocsi, null, null, null, null, null, null, true, true,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        ISDNAddressStringImpl msisdn = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "79161234567");
        final ExtBasicServiceCodeImpl basicServiceCode = new ExtBasicServiceCodeImpl(new ExtTeleserviceCodeImpl(TeleserviceCodeValue.allTeleservices));
        List<ExtBasicServiceCode> extBasicServiceCodeList=new ArrayList<ExtBasicServiceCode>();
        extBasicServiceCodeList.add(basicServiceCode);
        final MSISDNBSImpl msisdnbs = new MSISDNBSImpl(msisdn, extBasicServiceCodeList, null);

        final ExtCwFeatureImpl extCwFeature = new ExtCwFeatureImpl(new ExtBasicServiceCodeImpl(new ExtBearerServiceCodeImpl(
                BearerServiceCodeValue.allAsynchronousServices)), new ExtSSStatusImpl(true, false, false, false));
        
        List<ExtCwFeature> extCwFeatureList=new ArrayList<ExtCwFeature>();
        extCwFeatureList.add(extCwFeature);
        CallWaitingDataImpl callWaitingData = new CallWaitingDataImpl(extCwFeatureList, false);

        CallHoldDataImpl callHoldData = new CallHoldDataImpl(new ExtSSStatusImpl(false, true, false, false), true);

        ClipDataImpl clipData = new ClipDataImpl(new ExtSSStatusImpl(false, true, false, false), OverrideCategory.overrideEnabled, false);

        ClirDataImpl clirData = new ClirDataImpl(new ExtSSStatusImpl(false, true, false, false), CliRestrictionOption.permanent, true);

        EctDataImpl ectData = new EctDataImpl(new ExtSSStatusImpl(false, true, false, false), false);

        List<CSGSubscriptionData> csgSubscriptionDataList = new ArrayList<CSGSubscriptionData>();
        CSGIdImpl csgId = new CSGIdImpl();
        CSGSubscriptionDataImpl csgSubscriptionData = new CSGSubscriptionDataImpl(csgId, null, null, null);
        csgSubscriptionDataList.add(csgSubscriptionData);
        
        List<MSISDNBS> msisdnBSList=new ArrayList<MSISDNBS>();
        msisdnBSList.add(msisdnbs);
        AnyTimeSubscriptionInterrogationResponseImpl response = new AnyTimeSubscriptionInterrogationResponseImpl(callForwardingData,
                callBarringData, odbInfo, camelSubscriptionInfo, new SupportedCamelPhasesImpl(true, true, true, true),
                new SupportedCamelPhasesImpl(true, true, false, false), MAPExtensionContainerTest.GetTestExtensionContainer(),
                new OfferedCamel4CSIsImpl(true, true, true, true, true, true, true), new OfferedCamel4CSIsImpl(true, true, true, true, false, false, false),
                msisdnBSList, csgSubscriptionDataList, callWaitingData, callHoldData, clipData, clirData, ectData);

        ByteBuf buffer=parser.encode(response);
        byte[] encodedData = new byte[buffer.readableBytes()];
        buffer.readBytes(encodedData);
        assertTrue(Arrays.equals(data, encodedData));
    }
}