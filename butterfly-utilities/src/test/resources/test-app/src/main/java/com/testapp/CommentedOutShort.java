//
//
// package com.paypal.test.vi.fdr8583;
//
// import java.io.BufferedReader;
// import java.io.BufferedWriter;
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.io.IOException;
// import java.io.UnsupportedEncodingException;
// import java.net.Socket;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.logging.Level;
// import org.apache.commons.lang.StringUtils;
// import org.jpos.iso.BaseChannel;
// import org.jpos.iso.ISOException;
// import org.jpos.iso.ISOMsg;
// import org.jpos.iso.ISOUtil;
// import org.jpos.iso.packager.GenericPackager;
// import org.jpos.iso.packager.XMLPackager;
// import org.jpos.util.Logger;
// import org.jpos.util.SimpleLogListener;
// import org.json.JSONException;
// import org.json.JSONObject;
// import com.paypal.test.bluefin.platform.asserts.BluefinAsserts;
// import com.paypal.test.jaws.file.StageFileHelper;
// import com.paypal.test.vi.logging.ApplicationLogger;
//
//
// public class FDR8583RegressionClient {
// final protected static int bufSize = 0xFFFF + 2;
// final protected static String msgConfigFile =
// "src/test/resources/jpos/fdr/fdr_iso_packager.xml";
// final protected static String requestFile =
// "src/test/resources/jpos/fdr/request.xml";
// final protected static String logonResponseMsgFile =
// "src/test/resources/jpos/fdr/logon-network-response.xml";
// final protected static String echoResponseMsgFile =
// "src/test/resources/jpos/fdr/echo-network-response.xml";
// final protected static String logOffResponseMsgFile =
// "src/test/resources/jpos/fdr/logoff-network-response.xml";
// final protected static String BALANCE_INQUIRY = "BALANCE_INQUIRY";
// final protected static String BALANCE_INQUIRY_ATM = "BALANCE_INQUIRY_ATM";
// public static BaseChannel channel;
//
// public static Socket clientSocket;
// public static BufferedReader is = null;
// public static BufferedWriter os = null;
// public static boolean checkMissingField = false;
//
// private static boolean functionalTest = false;
//
// // The following number is the number of lines to read at the end of the
// // application log file in order to get all
// // the data of the transaction. Note that the average transaction only takes
// // about 250 lines, but the number of
// // lines read should be bigger than that because:
// // -the logger could be more verbose in the future
// // -there may be other transactions going on at the same time which will add
// // lines in between the ones we want
// // The number should be large enough to guarantee all lines will be read,
// // but small enough that it doesn't impact performance.
// // Preliminary tests show that reading 1500 lines takes less than 1 second
// // on average
// private static final int NUM_LINES_TO_READ = 1500;
//
// public void viCT2Test() throws Exception {
//
// final FDR8583MsgPojo outMsgPojo = new FDR8583MsgPojo();
//
// outMsgPojo.serverHost = "localhost";
// outMsgPojo.serverPort = 2000;
//
// final Map<Integer, String> dataMap = new HashMap<Integer, String>();
// try {
// final ISOMsg request = getISOMsg(requestFile);
// final int maxFld = request.getMaxField();
// for (int i = 0; i <= maxFld; i++) {
// final String value = request.getString(i);
// if (value != null) {
// dataMap.put(i, value);
// }
// }
// } catch (final Exception e) {
// e.printStackTrace();
// }
//
// // outMsgPojo.h14_rejectCode = "635";
// for (final Map.Entry<Integer, String> entry : dataMap.entrySet()) {
// ApplicationLogger.getLogger().info("Key : " + entry.getKey() + " Value : " +
// entry.getValue());
//
// outMsgPojo.msgFields.set(entry.getKey(), entry.getValue());
// }
// /*
// * outMsgPojo.msgFields.set(0, "0100"); outMsgPojo.msgFields.set(2,
// "4321432143214321"); outMsgPojo.msgFields.set(3, "103020");
// outMsgPojo.msgFields.set(4, "12345");
// */
// FDR8583MsgPojo inMsgPojo = null;
// try {
// inMsgPojo = FDR8583RegressionClient.exec(outMsgPojo, null, null);
// } catch (final IOException e) {
// ApplicationLogger.getLogger().log(Level.SEVERE, "IOException :", e);
// e.printStackTrace();
// } catch (final ISOException e) {
// ApplicationLogger.getLogger().log(Level.SEVERE, "ISOException :", e);
// e.printStackTrace();
// } catch (final Exception e) {
// ApplicationLogger.getLogger().log(Level.SEVERE, "Exception :", e);
// e.printStackTrace();
// }
// ApplicationLogger.getLogger().info("end main.");
//
// }
//
// public static FDR8583MsgPojo exec(FDR8583MsgPojo outMsgPojo, FDR8583Data
// dataRow, GenericPackager genericPackager) throws Exception {
// // init logger
// final Logger logger = new Logger();
// logger.addListener(new SimpleLogListener(System.out));
//
// final ISOMsg outIsoMsg = new ISOMsg();
// if (genericPackager == null) {
// outIsoMsg.setPackager(new GenericPackager(new
// FileInputStream(msgConfigFile)));
// } else {
// outIsoMsg.setPackager(genericPackager);
// }
// pojoToMsg(outMsgPojo, outIsoMsg);
//
// if (outIsoMsg.getString(0).equals("0100") ||
// outIsoMsg.getString(0).equals("0200")) {
// outIsoMsg.unset(38);
// outIsoMsg.unset(39);
// }
// if (outIsoMsg.getString(0).equals("0120") ||
// outIsoMsg.getString(0).equals("0220")) {
// outIsoMsg.set(90, getOriginalDataElement(outIsoMsg, outIsoMsg));
// }
// channel.send(outIsoMsg);
// outIsoMsg.dump(System.out, " ");
//
// final ISOMsg inIsoMsg = getNonNetworkInMsg(channel);
//
// inIsoMsg.dump(System.out, " ");
//
// final boolean hasFollowupMessage = dataRow != null &&
// StringUtils.isNotEmpty(dataRow.getOut0FollowupMessageTypeIndicator());
// if (functionalTest && !hasFollowupMessage) {
// validateApplicationLogs(outIsoMsg, inIsoMsg, getEndOfLogFile(), dataRow);
// }
//
// validateInboundMessage(outIsoMsg, inIsoMsg, dataRow);
// if (hasFollowupMessage) {
// outIsoMsg.setMTI(dataRow.getOut0FollowupMessageTypeIndicator());
// createTrailerISOMsg(outIsoMsg, inIsoMsg, dataRow);
// channel.send(outIsoMsg);
// outIsoMsg.dump(System.out, " ");
// final ISOMsg inTrailerIsoMsg = getNonNetworkInMsg(channel);
// inTrailerIsoMsg.dump(System.out, " ");
// if (functionalTest) {
// validateApplicationLogs(outIsoMsg, inTrailerIsoMsg, getEndOfLogFile(),
// dataRow);
// }
// validateInboundMessage(outIsoMsg, inTrailerIsoMsg, dataRow);
// }
//
// // convert inbound message to pojo
// return bufToPojo(inIsoMsg);
// }
//
// // Get a non-network in message from the given channel. This is needed
// // because the network message
// // can potentially come in between the outbound and inbound message
// private static ISOMsg getNonNetworkInMsg(BaseChannel channel) throws
// ISOException, IOException {
// ISOMsg inIsoMsg = channel.receive();
// while (inIsoMsg.getMTI().startsWith("08")) {
// inIsoMsg = channel.receive();
// }
// return inIsoMsg;
// }
//
// private static void validateApplicationLogs(ISOMsg outIsoMsg, ISOMsg
// inIsoMsg, List<String> logs, FDR8583Data dataRow) throws ISOException {
// final String stan = (String) inIsoMsg.getValue(11);
// try {
// final FDRFunctionalTest functionalTests = new FDRFunctionalTest();
// for (final String line : logs) {
// if (line.contains(stan) && line.contains("message_type\":\"" +
// functionalTests.getMessageType(outIsoMsg.getMTI()))) {
// if (line.contains("PaymentValidationRequest")) {
// final String text = line.substring(line.indexOf("{"));
// System.out.println("PaymentValidationRequest" + text);
// functionalTests.validatePaymentValidationRequest(outIsoMsg, new
// JSONObject(text));
// } else if (line.contains("MessageBrokerAsyncHandler")) {
// final String text = line.substring(line.indexOf("{"));
// System.out.println("MessageBrokerAsyncHandler" + text);
// functionalTests.validateSwitchCanonicalObject(outIsoMsg, new
// JSONObject(text), false, dataRow);
// } else if (line.contains("ResponseMapper")) {
// final String text = line.substring(line.indexOf("{"));
// System.out.println("ResponseMapper" + text);
// functionalTests.validateSwitchCanonicalObject(inIsoMsg, new JSONObject(text),
// true, dataRow);
// }
// }
// }
// } catch (final JSONException e) {
// e.printStackTrace();
// }
// }
//
// private static void validateInboundMessage(ISOMsg outIsoMsg, ISOMsg inIsoMsg,
// FDR8583Data dataRow) {
// if (inIsoMsg == null) {
// throw new RuntimeException("Invalid inIsoMsg");
// }
// BluefinAsserts.assertTrue(true, String.format("Verifying response for message
// - [%s] = Response MTI [%s], STAN - [%s]", outIsoMsg.getString(0),
// inIsoMsg.getString(0), inIsoMsg.getString(11)));
// // if (inIsoMsg.getString(0).equals("0110") ||
// // inIsoMsg.getString(0).equals("0210")) {
// // BluefinAsserts.assertTrue(inIsoMsg.getString(38) != null,
// // "Auth Code returned");
// // }
// // BluefinAsserts.assertEquals(inIsoMsg.getString(39),
// // dataRow.getIn39ResponseCode(), "Verify Response Code");
// if (checkMissingField) {
// final String missingFieldsString = getResponse(inIsoMsg.getString("11"));
// if (StringUtils.isNotEmpty(missingFieldsString)) {
// BluefinAsserts
// .fail(String.format("Missing Field(s) in request fieldName - [%s] for MTI -
// [%s], Msg Stan - [%s]", missingFieldsString, outIsoMsg.getString(0),
// inIsoMsg.getString("11")));
// }
// }
//
// final String txnType = getTxnType(outIsoMsg);
// System.out.println("Transaction Type:" + txnType);
// BluefinAsserts.verifyEquals(inIsoMsg.getString(11), outIsoMsg.getString(11),
// String.format("Verifying STAN for [%s]", inIsoMsg.getString(0)));
// if (StringUtils.isEmpty(dataRow.getOut0FollowupMessageTypeIndicator()) ||
// dataRow.getOut0FollowupMessageTypeIndicator().equals(outIsoMsg.getString(0))
// || !"05".equals(dataRow.getOut39ResponseCode())) {
// if (null != txnType && (txnType.equals(BALANCE_INQUIRY) ||
// txnType.equals(BALANCE_INQUIRY_ATM))) {
// validateBalanceInquiryResponse(inIsoMsg, dataRow);
// } else if (("0110".equals(inIsoMsg.getString(0))) &&
// dataRow.getOut3ProcessingCode().equals("230000")) {
// BluefinAsserts.assertTrue(inIsoMsg.getString(38) == null, String.format("Auth
// Code [%s] returned for -[%s]", inIsoMsg.getString(38),
// inIsoMsg.getString(0)));
// } else if (!("220000".equals(inIsoMsg.getString(3))) &&
// ("0110".equals(inIsoMsg.getString(0)) ||
// "0210".equals(inIsoMsg.getString(0))) &&
// dataRow.getIn39ResponseCode().equals("00")) {
// BluefinAsserts.assertTrue(inIsoMsg.getString(38) != null, String.format("Auth
// Code [%s] returned for -[%s]", inIsoMsg.getString(38),
// inIsoMsg.getString(0)));
// }
// BluefinAsserts.verifyEquals(inIsoMsg.getString(39),
// dataRow.getIn39ResponseCode(),
// String.format("Verifying Response Code - [%s] for [%s]",
// dataRow.getIn39ResponseCode(), inIsoMsg.getString(0)));
// } else {
// // BluefinAsserts.assertTrue(inIsoMsg.getString(39) != null,
// // "Invalid Response returned " + inIsoMsg.getString(0));
// BluefinAsserts.verifyEquals(inIsoMsg.getString(39),
// dataRow.getIn39ResponseCode(),
// String.format("Verifying Response Code - [%s] for [%s]",
// dataRow.getIn39ResponseCode(), inIsoMsg.getString(0)));
// }
//
// System.out.println("transaction type:" + txnType);
// if (inIsoMsg.getString(0).equals("0110") && txnType != null &&
// !txnType.equals(BALANCE_INQUIRY) && !txnType.equals(BALANCE_INQUIRY_ATM)) {
// BluefinAsserts.assertEquals(inIsoMsg.getString(123), "TDAR01XCR01M", "Verify
// AV and CV");
// }
//
// if (inIsoMsg.getString(0).equals("0210") ||
// inIsoMsg.getString(0).equals("0230")) {
// BluefinAsserts.assertEquals(inIsoMsg.getString(63),
// dataRow.getOut63EFTData());
// }
// }
//
// private static String getTxnType(ISOMsg outIsoMsg) {
// String txnType = null;
// String processingCodeString = null;
// if (outIsoMsg.getString(3) != null && outIsoMsg.getString(3).length() == 6) {
// processingCodeString = outIsoMsg.getString(3).substring(0, 2);
// }
// switch (processingCodeString) {
// case "30":
// case "39":
// txnType = BALANCE_INQUIRY;
// case "31":
// txnType = BALANCE_INQUIRY_ATM;
// }
// return txnType;
// }
//
// private static void validateBalanceInquiryResponse(ISOMsg inIsoMsg,
// FDR8583Data dataRow) {
// if (("0110".equals(inIsoMsg.getString(0)) ||
// "0210".equals(inIsoMsg.getString(0))) && inIsoMsg.getString(39).equals("00"))
// {
// BluefinAsserts.assertTrue(inIsoMsg.getString(54) != null);
// BluefinAsserts.assertEquals(inIsoMsg.getString(54),
// dataRow.getIn54AdditionalAmount(),
// "BALANCE_INQUIRY" + " *** Actual in54AdditionalAmount: " +
// inIsoMsg.getString(54) + " *** Expected: " +
// dataRow.getIn54AdditionalAmount());
// } else {
// BluefinAsserts.assertTrue(inIsoMsg.getString(54) == null, "Balance Amount is
// not populated for this messagetype " + inIsoMsg.getString(0));
// }
// }
//
// private static void createTrailerISOMsg(ISOMsg outIsoMsg, ISOMsg inIsoMsg,
// FDR8583Data dataRow) throws Exception {
// if (outIsoMsg.getMTI().equals("0120") || outIsoMsg.getMTI().equals("0121")) {
// createTrailer120ISOMsg(outIsoMsg, inIsoMsg, dataRow);
// } else if (outIsoMsg.getMTI().equals("0220") ||
// outIsoMsg.getMTI().equals("0221")) {
// createTrailer220ISOMsg(outIsoMsg, inIsoMsg, dataRow);
// } else if (outIsoMsg.getMTI().equals("0420") ||
// outIsoMsg.getMTI().equals("0421")) {
// createTrailer420ISOMsg(outIsoMsg, inIsoMsg, dataRow);
// } else if (outIsoMsg.getMTI().equals("0400") ||
// outIsoMsg.getMTI().equals("0401")) {
// createTrailer400ISOMsg(outIsoMsg, inIsoMsg, dataRow);
// }
// }
//
// private static void createTrailer120ISOMsg(ISOMsg outIsoMsg, ISOMsg inIsoMsg,
// FDR8583Data dataRow) throws Exception {
// outIsoMsg.set(5, inIsoMsg.getString(4));
// outIsoMsg.unset(16);
// outIsoMsg.unset(28);
// outIsoMsg.unset(33);
// if (dataRow != null && StringUtils.isNotEmpty(dataRow.getIn39ResponseCode())
// && "00".equals(dataRow.getIn39ResponseCode())) {
// outIsoMsg.set(38, inIsoMsg.getString(38));
// }
// outIsoMsg.set(39, dataRow.getOut39ResponseCode());
// outIsoMsg.set(60, "4043");
// outIsoMsg.set(90, getOriginalDataElement(inIsoMsg, outIsoMsg));
// outIsoMsg.unset(104);
// }
//
// private static void createTrailer220ISOMsg(ISOMsg outIsoMsg, ISOMsg inIsoMsg,
// FDR8583Data dataRow) throws Exception {
// outIsoMsg.set(5, inIsoMsg.getString(4));
// outIsoMsg.unset(16);
// outIsoMsg.unset(28);
// outIsoMsg.unset(33);
// if (dataRow != null && StringUtils.isNotEmpty(dataRow.getIn39ResponseCode())
// && "00".equals(dataRow.getIn39ResponseCode())) {
// outIsoMsg.set(38, inIsoMsg.getString(38));
// }
// outIsoMsg.set(39, dataRow.getOut39ResponseCode());
// outIsoMsg.set(59, dataRow.getOut59GeoData());
// outIsoMsg.set(90, getOriginalDataElement(inIsoMsg, outIsoMsg));
//
// }
//
// private static void createTrailer420ISOMsg(ISOMsg outIsoMsg, ISOMsg inIsoMsg,
// FDR8583Data dataRow) throws Exception {
// outIsoMsg.set(5, inIsoMsg.getString(4));
// outIsoMsg.unset(16);
// outIsoMsg.unset(28);
// outIsoMsg.unset(33);
// if (dataRow != null && StringUtils.isNotEmpty(dataRow.getIn39ResponseCode())
// && "00".equals(dataRow.getIn39ResponseCode())) {
// outIsoMsg.set(38, inIsoMsg.getString(38));
// }
// outIsoMsg.set(39, dataRow.getOut39ResponseCode());
// outIsoMsg.set(59, dataRow.getOut59GeoData());
// outIsoMsg.set(60, "8026");
// outIsoMsg.set(90, getOriginalDataElement(inIsoMsg, outIsoMsg));
// outIsoMsg.unset(123);
// }
//
// private static void createTrailer400ISOMsg(ISOMsg outIsoMsg, ISOMsg inIsoMsg,
// FDR8583Data dataRow) throws Exception {
// if (dataRow != null && StringUtils.isNotEmpty(dataRow.getIn39ResponseCode())
// && "00".equals(dataRow.getIn39ResponseCode())) {
// outIsoMsg.set(38, inIsoMsg.getString(38));
// }
// outIsoMsg.set(39, dataRow.getOut39ResponseCode());
// outIsoMsg.unset(104);
// outIsoMsg.unset(111);
// outIsoMsg.unset(123);
// }
//
// private static String getOriginalDataElement(ISOMsg inIsoMsg, ISOMsg
// outIsoMsg) {
// String mti = "";
// if (inIsoMsg.getString(0).equals("0110")) {
// mti = "0100";
// } else if (inIsoMsg.getString(0).equals("0210")) {
// mti = "0200";
// } else if (inIsoMsg.getString(0).equals("0410")) {
// mti = "0400";
// } else {
// mti = outIsoMsg.getString(0);
// }
// return mti + outIsoMsg.getString(11) + outIsoMsg.getString(7) +
// outIsoMsg.getString(37) + "0000000000";
// }
//
// protected static void packIsoMsg(String fileName, ISOMsg msg) throws
// Exception {
// final File f = new File(fileName);
// if (!f.exists() || !f.isFile()) {
// throw new Exception("Invalid path of ISO message xml file. Could not
// continue.");
// }
// final FileInputStream fis = new FileInputStream(f);
// msg.setPackager(new XMLPackager());
// msg.unpack(fis);
// msg.setPackager(new org.jpos.iso.packager.GenericPackager(msgConfigFile));
// msg.setDirection(ISOMsg.OUTGOING);
// }
//
// private static FDR8583MsgPojo bufToPojo(ISOMsg inIsoMsg) throws
// FileNotFoundException, ISOException, UnsupportedEncodingException {
// // init logger
// final Logger logger = new Logger();
// /*
// * logger.addListener(new SimpleLogListener(System.out)); // init packager
// GenericPackager genericPackager = new GenericPackager( new
// FileInputStream(msgConfigFile)); genericPackager.setLogger(logger,
// "Paypal8583Client"); // get inbound msg header FDR8583LenHeader inHeader =
// new FDR8583LenHeader(buf); // get inbound message body ISOMsg inIsoMsg = new
// ISOMsg(); inIsoMsg.setPackager(genericPackager);
// inIsoMsg.unpack(getMsgBodyFromBuf(buf));
// */
//
// // dump message content to pojo and return
// final FDR8583MsgPojo inMsgPojo = new FDR8583MsgPojo();
// inMsgPojo.rawMsg = ISOUtil.hexString(inIsoMsg.pack());
// msgToPojo(inIsoMsg, inMsgPojo);
// return inMsgPojo;
// }
//
// private static ISOMsg getISOMsg(String requestFileName) throws Exception {
// final File f = new File(requestFileName);
// if (!f.exists() || !f.isFile()) {
// throw new Exception("Invalid path of ISO message xml file. Could not
// continue.");
// }
// final FileInputStream fis = new FileInputStream(f);
// final ISOMsg requestISOMsg = new ISOMsg();
// requestISOMsg.setPackager(new XMLPackager());
// requestISOMsg.unpack(fis);
// requestISOMsg.setPackager(new org.jpos.iso.packager.GenericPackager());
// return requestISOMsg;
// }
//
// private static void pojoToMsg(FDR8583MsgPojo msgPojo, ISOMsg isoMsg) throws
// ISOException {
// // populate msg body
// if (msgPojo == null || isoMsg == null) {
// return;
// }
// for (int i = 0; i <= FDR8583MsgPojo.maxMsgField; i++) {
// isoMsg.set(i, msgPojo.msgFields.get(i));
// }
// }
//
// private static void msgToPojo(ISOMsg isoMsg, FDR8583MsgPojo msgPojo) throws
// ISOException {
// // populate header value
// if (isoMsg == null || msgPojo == null) {
// return;
// // msgPojo.headerMsgLen = header.getLen();
// }
//
// // populate msg body
// for (int i = 0; i <= FDR8583MsgPojo.maxMsgField; i++) {
// msgPojo.msgFields.set(i, isoMsg.getString(i));
// }
// }
//
// public static byte[] getMsgBodyFromBuf(byte[] buf) {
// final FDR8583LenHeader inHeader = new FDR8583LenHeader(buf);
// if (inHeader.getLen() + FDR8583LenHeader.LENGTH > buf.length) {
// throw new RuntimeException("buf too short");
// }
//
// final byte[] c = new byte[inHeader.getLen()];
// System.arraycopy(buf, FDR8583LenHeader.LENGTH, c, 0, inHeader.getLen());
// return c;
// }
//
// public static String getResponse(String data) {
// String response = null;
// try {
// os.write(data);
// os.newLine();
// os.flush();
// response = is.readLine();
// return response;
// } catch (final Exception e) {
// e.printStackTrace();
// ApplicationLogger.getLogger().log(Level.SEVERE, "Socket read Error", e);
// }
// return null;
// }
//
// public static void closeResource() {
// try {
// if (is != null) {
// is.close();
// }
// if (os != null) {
// os.close();
// }
// if (clientSocket != null) {
// clientSocket.close();
// }
// ApplicationLogger.getLogger().info("Connection Closed...");
// } catch (final IOException ex) {
// }
// }
//
// private static List<String> getEndOfLogFile() throws IOException {
// return
// StageFileHelper.getFileContents("/x/web/LIVE/switchfdriso8583serv/log/switchfdriso8583serv.log",
// NUM_LINES_TO_READ);
// }
//
// public void setFunctionalTest(boolean functionalTest) {
// FDR8583RegressionClient.functionalTest = functionalTest;
// }
// }