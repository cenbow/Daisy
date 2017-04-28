/**
 * 
 */
package com.yourong.common.thirdparty.sinapay.member.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCardType;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import com.yourong.common.thirdparty.sinapay.common.enums.BankServiceType;
import com.yourong.common.thirdparty.sinapay.common.enums.CharsetType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.ResponseCodeEnum;
import com.yourong.common.thirdparty.sinapay.common.enums.SignType;
import com.yourong.common.thirdparty.sinapay.common.enums.VerifyStatus;
import com.yourong.common.thirdparty.sinapay.common.util.CastStringUtil;
import com.yourong.common.thirdparty.sinapay.common.util.RequestUtil;
import com.yourong.common.thirdparty.sinapay.common.util.ResponseUtil;
import com.yourong.common.thirdparty.sinapay.common.util.sign.RSA;
import com.yourong.common.thirdparty.sinapay.member.common.Constants;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.BalanceFreezeDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.BalanceUnFreezeDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.BindingBankCardAdvanceDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.BindingBankCardDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.BindingVerifyDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.CreateActiveMemberDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.PayPasswordDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.PersonRealnameDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryAccountDetailsDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryBalanceDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryBankCardDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryCtrlResultDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryVerifyDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.UnBindingBankCardDto;
import com.yourong.common.thirdparty.sinapay.member.domain.result.AccountBalance;
import com.yourong.common.thirdparty.sinapay.member.domain.result.AccountDetail;
import com.yourong.common.thirdparty.sinapay.member.domain.result.BankCardEntry;
import com.yourong.common.thirdparty.sinapay.member.domain.result.CardBindingResult;
import com.yourong.common.thirdparty.sinapay.member.domain.result.StatementEntry;
import com.yourong.common.thirdparty.sinapay.member.exception.InvalidFieldSizeException;
import com.yourong.common.thirdparty.sinapay.member.exception.MemberGatewayInvokeFailureException;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.HandleWithholdAuthorityDto;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.common.util.ValueCheckUtil;

/**
 * 会员网关前置的实现类。
 * <p>调用各服务方法前应先完成一些参数设置。签名私钥,私钥的保护口令,keyStore的保护口令，微钱包的公钥,公私钥别名,和在微钱包开设的合作者Id等属性是必须设置的！服务名,接口版本如果需要覆盖默认值，
 * 需通过在{@link Properties}中设置新值来实现。连接超时时间与响应超时时间如果不设则会使用默认值。
 * @author guoyongqiang
 * @version 0.1 2014年5月21日 下午2:18:00
 */
@Service
public class MemberClient implements MemberGatewayFront, InitializingBean {

    private static Logger logger       = LoggerFactory.getLogger(MemberClient.class);
    //MD5的KEY
    private String        memberMd5KeyPath;
    //RSA加密的KEY
    private String        memberPublicKey;
    //网关的URL
    private String        memberGatewayUrl;
    //默认的签名版本
    private String        memberDefaultSignVersion;
    //默认加密的版本号
    private String        memberDefaultEncyptVersion;
    //默认的服务版本号
    private String        memberDefaultServerVersion;
    //商户ID
    private String        memberDefaultParterId;

    private String        memberMd5Key = null;

    //字段加密的公钥
    private  String     encrytPublicKey;
    //RSA公钥
    private String    rsaPublicSiginKey;
    //RSA私钥
    private String   rsaPrivateSiginKey;

    public void setRsaPublicSiginKey(String rsaPublicSiginKey) {
        this.rsaPublicSiginKey = rsaPublicSiginKey;
    }

    public void setRsaPrivateSiginKey(String rsaPrivateSiginKey) {
        this.rsaPrivateSiginKey = rsaPrivateSiginKey;
    }

    public void setEncrytPublicKey(String encrytPublicKey) {
        this.encrytPublicKey = encrytPublicKey;
    }

    public String getRsaPublicSiginKey() {
        return rsaPublicSiginKey;
    }

    public String getRsaPrivateSiginKey() {
        return rsaPrivateSiginKey;
    }

    public String getEncrytPublicKey() {
        return encrytPublicKey;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        FileInputStream md5InputStream = null;
        try {
            if (memberMd5Key == null) {
                md5InputStream = new FileInputStream(new File(memberMd5KeyPath));
                List<String> readLines = IOUtils.readLines(md5InputStream,
                    CharsetType.UTF8.charset());
                Preconditions.checkArgument(readLines != null && !readLines.isEmpty(),
                    "MD5KEY文件中没有内容");
                memberMd5Key = StringUtils.trim(readLines.get(0));
            }
        } catch (FileNotFoundException e) {
            logger.error("从路径{}找不到MD5的KEY文件", memberMd5KeyPath);
            throw new MemberGatewayInvokeFailureException(
                "从路径" + memberMd5KeyPath + "找不到MD5的KEY文件", e);
        } catch (IOException e) {
            logger.error("从路径{}读取MD5文件异常", memberMd5KeyPath);
            throw new MemberGatewayInvokeFailureException("从路径" + memberMd5KeyPath + "读取MD5文件异常", e);
        } finally {
            if (md5InputStream != null) {
                IOUtils.closeQuietly(md5InputStream);
            }
        }

    }

    @Override
    public ResultDto<?> createActiveMember(CreateActiveMemberDto activeMemberDto)
                                                                                 throws MemberGatewayInvokeFailureException {
        try {
            //字段校验
            Preconditions.checkNotNull(activeMemberDto, "activeMemberDto can not be null");
            checkFieldNotnull(activeMemberDto);

            //请求的数据
            TreeMap<String, String> requestMap = Maps.newTreeMap();
            buildBaseParams(requestMap, Constants.SERVICE_CREATE_ACTIVE_MEMBER,
                memberDefaultParterId, activeMemberDto.getCharsetType(), activeMemberDto.getMemo(),
                getRequestTimeString());
            buildActiveMemberParams(requestMap, activeMemberDto);
            //调用信息打点
           logger.info("创建会员[createActiveMember],请求参数为{}", requestMap);

            //结果处理 
            return request(requestMap, SignType.RSA, activeMemberDto.getCharsetType());
        } catch (Exception e) {
            throw new MemberGatewayInvokeFailureException(e);
        }
    }

    /**
     * 校验带有<code>@Notnull</code>注解的字段不能为空
     * @param object
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private void checkFieldNotnull(Object object) throws IllegalAccessException,
                                                 InvocationTargetException, NoSuchMethodException {
        Preconditions.checkArgument(ArrayUtils.isNotEmpty(object.getClass().getDeclaredFields()),
            "[object=%s] do not has fields", object.getClass().getSimpleName());

        Field[] declaredFields = relfect2LevelFields(object);
        for (Field field : declaredFields) {
            NotNull notNullAnno = field.getAnnotation(NotNull.class);
            if (notNullAnno != null) {
                String fieldName = field.getName();
                Preconditions.checkNotNull(PropertyUtils.getProperty(object, fieldName),
                    "[Field=%s] can not be null", fieldName);
            }
        }
    }

    /**
     * 当前对象往上找2层，找出其所有fields
     * @param obj
     * @return
     */
    public static Field[] relfect2LevelFields(Object obj) {
        // 当前类，第一层
        Field[] curDeclaredFields = obj.getClass().getDeclaredFields();
        // 基类，第二层
        Class<?> fClazz = obj.getClass().getSuperclass();
        if (fClazz != null) {
            Field[] appendFClazz = appendSuperClassFields(curDeclaredFields,
                fClazz.getDeclaredFields());
            return appendFClazz;
        }
        return curDeclaredFields;
    }

    /**
        * 加上基类的所有成员
        * @param curDeclaredFields
        */
    public static Field[] appendSuperClassFields(Field[] curDeclaredFields,
                                                 Field[] superDeclaredFields) {
        if (ArrayUtils.isNotEmpty(superDeclaredFields)) {
            Field[] resultFields = new Field[curDeclaredFields.length + superDeclaredFields.length];
            System.arraycopy(curDeclaredFields, 0, resultFields, 0, curDeclaredFields.length);
            System.arraycopy(superDeclaredFields, 0, resultFields, curDeclaredFields.length,
                superDeclaredFields.length);

            return resultFields;
        }
        return curDeclaredFields;
    }

    private static void buildActiveMemberParams(Map<String, String> buildSignParams, CreateActiveMemberDto activeMemberDto) {
        buildSignParams.put("identity_id", activeMemberDto.getIdentityId());
        buildSignParams.put("identity_type", activeMemberDto.getIdType().name());
        if (StringUtil.isNotBlank(activeMemberDto.getClientIp())) {
        	buildSignParams.put("client_ip", activeMemberDto.getClientIp());
        }
        if (activeMemberDto.getMemberType() != null)
            buildSignParams.put("member_type", "" + activeMemberDto.getMemberType().getType());
        if (activeMemberDto.getExtendParam() != null && !activeMemberDto.getExtendParam().isEmpty())
            buildSignParams.put("extend_param", map2FormatStyle(activeMemberDto.getExtendParam()));
    }

    @Override
    public ResultDto<?> setRealname(PersonRealnameDto realNameDto)
                                                                  throws MemberGatewayInvokeFailureException {
        try {
            //字段校验
            Preconditions.checkNotNull(realNameDto, "realNameDto can not be null");
            checkFieldNotnull(realNameDto);

            //请求的数据
            HashMap<String, String> requestMap = Maps.newHashMap();
            buildBaseParams(requestMap, Constants.SERVICE_SET_REAL_NAME, memberDefaultParterId,
                realNameDto.getCharsetType(), realNameDto.getMemo(), getRequestTimeString());
            buildRealNameParam(requestMap, realNameDto);

            //调用信息打点
            logger.info("实名认证[setRealname],请求参数为{}", requestMap);

            //结果处理
            return request(requestMap, SignType.RSA, realNameDto.getCharsetType());
        } catch (Exception e) {
            throw new MemberGatewayInvokeFailureException(e);
        }
    }

    private void buildRealNameParam(Map<String, String> buildSignParams,
                                    PersonRealnameDto realNameDto) throws Exception {
        buildSignParams.put("identity_id", realNameDto.getIdentityId());
        buildSignParams.put("identity_type", realNameDto.getIdentityType().name());
        buildSignParams.put("real_name", crytionByRSA(realNameDto.getRealName(), realNameDto.getCharsetType()));
        buildSignParams.put("cert_type", realNameDto.getCertType().name());
        buildSignParams.put("cert_no", crytionByRSA(realNameDto.getCertNo(), realNameDto.getCharsetType()));
        // 请求IP
        if (StringUtil.isNotBlank(realNameDto.getClientIp())) {
        	buildSignParams.put("client_ip", realNameDto.getClientIp());
        }
        if (realNameDto.getNeedConfirm() != null)
            buildSignParams.put("need_confirm", realNameDto.getNeedConfirm().booleanValue() ? "Y" : "N");
        if (realNameDto.getExtendParam() != null && !realNameDto.getExtendParam().isEmpty())
            buildSignParams.put("extend_param", map2FormatStyle(realNameDto.getExtendParam()));
    }

    @Override
    public ResultDto<CardBindingResult> bindBankCard(BindingBankCardDto bankCardDto)
                                                                                    throws MemberGatewayInvokeFailureException {
        try {
            //字段检验
            Preconditions.checkNotNull(bankCardDto, "bankCardDto can not be null");
            checkFieldNotnull(bankCardDto);

            //请求的数据
            HashMap<String, String> requestMap = Maps.newHashMap();
            buildBaseParams(requestMap, Constants.SERVICE_BINDING_BANK_CARD, memberDefaultParterId,
                bankCardDto.getCharsetType(), bankCardDto.getMemo(), getRequestTimeString());
            buildingBindBankCardDto(requestMap, bankCardDto);

            //调用信息打点
                logger.info("绑定银行卡[bindBankCard],请求参数为{}", requestMap);
            //响应结果
            String json = RequestUtil.buildRequest(memberGatewayUrl, requestMap, SignType.RSA
                .name(), this.rsaPrivateSiginKey, memberDefaultSignVersion, bankCardDto.getCharsetType()
                .charset());
            

                logger.info("绑定银行卡[bindBankCard],返回响应信息{}", json);


            JSONObject obj = JSONObject.parseObject(json);
            checkGatewayResponse(obj);

            boolean isSuccess = isSuccess(obj.getString("response_code"));
            ResultDto<CardBindingResult> result = new ResultDto<CardBindingResult>(isSuccess);
            if (isSuccess) {
                CardBindingResult cardBindingResult = new CardBindingResult();
				if (obj.containsKey("card_id") || obj.containsKey("ticket")) {
					cardBindingResult.setCardId(obj.getString("card_id"));
					cardBindingResult.setTicket(obj.getString("ticket"));
				} else {
					throw new RuntimeException("返回的结果中未找到card_id值");
				}

                if (obj.containsKey("is_verified")) {
                    VerifyStatus verifyStatus = VerifyStatus.valueOf(obj.getString("is_verified"));
                    cardBindingResult.setVerified(verifyStatus == VerifyStatus.Y ? true : false);
                }
                // 验证签名
                ResponseUtil.verifyResponseSignbyRSA(json, bankCardDto.getCharsetType(), rsaPublicSiginKey);
                result.setModule(cardBindingResult);
            } else {
                result.setErrorCode(obj.getString("response_code"));
                result.setErrorMsg(obj.getString("response_message"));
            }
            return result;
        } catch (Exception e) {
            logger.error("调用网关绑定银行卡服务失败。", e);
            throw new MemberGatewayInvokeFailureException(e);
        }
    }

    /**
     * 绑定银行卡推进
     * @param bankCardDto
     * @return
     * @throws MemberGatewayInvokeFailureException
     */
    public ResultDto<CardBindingResult> bindingBankCardAdvance(BindingBankCardAdvanceDto bankCardDto)throws MemberGatewayInvokeFailureException {

        try {
            //字段检验
            Preconditions.checkNotNull(bankCardDto, "BindingBankCardAdvanceDto can not be null");
            checkFieldNotnull(bankCardDto);

            //请求的数据
            HashMap<String, String> requestMap = Maps.newHashMap();
            buildBaseParams(requestMap, Constants.SERVICE_BINDING_BANK_ADVICE_CARD, memberDefaultParterId,
                    bankCardDto.getCharsetType(), bankCardDto.getMemo(), getRequestTimeString());
            buildingBindBankCardAdviceDto(requestMap, bankCardDto);

            //调用信息打点
                logger.info("绑定银行卡推进[bindBankCard],请求参数为{}", requestMap);

            //响应结果
            String json = RequestUtil.buildRequest(memberGatewayUrl, requestMap, SignType.RSA
                    .name(), this.rsaPrivateSiginKey, memberDefaultSignVersion, bankCardDto.getCharsetType()
                    .charset());
            //调用信息打点
            logger.info("绑定银行卡推进[bindBankCard],返回响应信息{}", json);

            JSONObject obj = JSONObject.parseObject(json);
            checkGatewayResponse(obj);

            boolean isSuccess = isSuccess(obj.getString("response_code"));
            ResultDto<CardBindingResult> result = new ResultDto<CardBindingResult>(isSuccess);
            if (isSuccess) {
                CardBindingResult cardBindingResult = new CardBindingResult();
                if (obj.containsKey("card_id"))
                    cardBindingResult.setCardId(obj.getString("card_id"));
                else
                    throw new RuntimeException("返回的结果中未找到card_id值");

                if (obj.containsKey("is_verified")) {
                    VerifyStatus verifyStatus = VerifyStatus.valueOf(obj.getString("is_verified"));
                    cardBindingResult.setVerified(verifyStatus == VerifyStatus.Y ? true : false);
                }
                // 验证签名
                ResponseUtil.verifyResponseSignbyRSA(json, bankCardDto.getCharsetType(),rsaPublicSiginKey);
                result.setModule(cardBindingResult);
            } else {
                result.setErrorCode(obj.getString("response_code"));
                result.setErrorMsg(obj.getString("response_message"));
            }
            return result;
        } catch (Exception e) {
            logger.error("调用网关绑定银行卡服务失败。", e);
            throw new MemberGatewayInvokeFailureException(e);
        }
    }

    /**
     *
     * @param buildSignParams
     * @param bankCardDto
     */
    private void buildingBindBankCardAdviceDto(HashMap<String, String> buildSignParams, BindingBankCardAdvanceDto bankCardDto) {
        buildSignParams.put("ticket", bankCardDto.getTicket());
        buildSignParams.put("valid_code", bankCardDto.getValidCode());
        if (StringUtil.isNotBlank(bankCardDto.getClientIp())) 
        	buildSignParams.put("client_ip", bankCardDto.getClientIp());
        if (bankCardDto.getExtendParam() != null && !bankCardDto.getExtendParam().isEmpty())
            buildSignParams.put("extend_param", map2FormatStyle(bankCardDto.getExtendParam()));
    }


    /**
     * 对内容进行RSA加密
     * @param original
     * @param charsetType
     * @return
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    private String crytionByRSA(String original, CharsetType charsetType) throws Exception {
        try {
            byte[] decryptByPublicKey = RSA.encryptByPublicKey(
                original.getBytes(charsetType.charset()), encrytPublicKey);
            return Base64.encodeBase64String(decryptByPublicKey);
        } catch (Exception e) {
            throw e;
        }
    }

//    public static void main(String[] args) throws Exception {
//        MemberClient client = new MemberClient();
//        BindingVerifyDto bindingVerifyDto = new BindingVerifyDto();
//        bindingVerifyDto.setCharsetType(CharsetType.UTF8);
//        bindingVerifyDto.setIdentityId(SerialNumberUtil.generateIdentityId(110800000081L));
//        bindingVerifyDto.setIdentityType(IdType.UID);
//        bindingVerifyDto.setVerifyEntity("13667019187");
//        bindingVerifyDto.setVerifyType(VerifyType.MOBILE);
//        client.bindingVerify(bindingVerifyDto);
//    }

    private void buildingBindBankCardDto(Map<String, String> buildSignParams,
                                         BindingBankCardDto bankCardDto) throws Exception {
    	buildSignParams.put("request_no", bankCardDto.getRequestNo());
        buildSignParams.put("identity_id", bankCardDto.getIdentityId());
        buildSignParams.put("identity_type", bankCardDto.getIdentityType().name());
        buildSignParams.put("bank_code", bankCardDto.getBankCode().name());
        buildSignParams.put("bank_account_no",crytionByRSA(bankCardDto.getBankAccountNo(), bankCardDto.getCharsetType()));
        if (bankCardDto.getAccountName() != null)
            buildSignParams.put("account_name",
                crytionByRSA(bankCardDto.getAccountName(), bankCardDto.getCharsetType()));
        if (bankCardDto.getCardType() != null)
            buildSignParams.put("card_type", bankCardDto.getCardType().name());
        if (bankCardDto.getCardAttr() != null)
            buildSignParams.put("card_attribute", bankCardDto.getCardAttr().name());
        if (bankCardDto.getCertType() != null)
            buildSignParams.put("cert_type", bankCardDto.getCertType().name());
        if (StringUtils.isNotBlank(bankCardDto.getCertNo()))
            buildSignParams.put("cert_no",
                crytionByRSA(bankCardDto.getCertNo(), bankCardDto.getCharsetType()));
        if (StringUtils.isNotBlank(bankCardDto.getPhoneNo()))
            buildSignParams.put("phone_no",
                crytionByRSA(bankCardDto.getPhoneNo(), bankCardDto.getCharsetType()));
        if (StringUtils.isNotBlank(bankCardDto.getValidityPeriod()))
            buildSignParams.put("validity_period",
                crytionByRSA(bankCardDto.getValidityPeriod(), bankCardDto.getCharsetType()));
        if (StringUtils.isNotBlank(bankCardDto.getVerificationValue()))
            buildSignParams.put("verification_value",
                crytionByRSA(bankCardDto.getVerificationValue(), bankCardDto.getCharsetType()));
        if (StringUtils.isNotBlank(bankCardDto.getProvince()))
            buildSignParams.put("province", bankCardDto.getProvince());
        if (StringUtils.isNotBlank(bankCardDto.getCity()))
            buildSignParams.put("city", bankCardDto.getCity());
        if (StringUtils.isNotBlank(bankCardDto.getBankBranch()))
            buildSignParams.put("bank_branch", bankCardDto.getBankBranch());
        if (bankCardDto.getVerifyMode() != null)
            buildSignParams.put("verify_mode", bankCardDto.getVerifyMode().name());
        if (bankCardDto.getExtendParam() != null && !bankCardDto.getExtendParam().isEmpty())
            buildSignParams.put("extend_param", map2FormatStyle(bankCardDto.getExtendParam()));
        if (StringUtils.isNotBlank(bankCardDto.getUserBindIp()))
            buildSignParams.put("client_ip", bankCardDto.getUserBindIp());
    }

    @Override
    public ResultDto<?> unbindBankCard(UnBindingBankCardDto unBindingBankCardDto)
                                                                                 throws MemberGatewayInvokeFailureException {
        try {
            //字段校验
            Preconditions
                .checkNotNull(unBindingBankCardDto, "unBindingBankCardDto can not be null");
            checkFieldNotnull(unBindingBankCardDto);

            //请求的数据
            HashMap<String, String> requestMap = Maps.newHashMap();
            buildBaseParams(requestMap, Constants.SERVICE_UNBINDING_BANK_CARD,
                memberDefaultParterId, unBindingBankCardDto.getCharsetType(),
                unBindingBankCardDto.getMemo(), getRequestTimeString());
            buildUnbindingBankCardDto(requestMap, unBindingBankCardDto);

            //调用信息打点
            logger.info("解绑银行卡[unbindBankCard],请求参数为{}", requestMap);

            return request(requestMap, SignType.RSA, unBindingBankCardDto.getCharsetType());
        } catch (Exception e) {
            throw new MemberGatewayInvokeFailureException(e);
        }
    }

    private static void buildUnbindingBankCardDto(Map<String, String> buildSignParams,
                                                  UnBindingBankCardDto unBindingBankCardDto) {
        buildSignParams.put("identity_id", unBindingBankCardDto.getIndentityId());
        buildSignParams.put("identity_type", unBindingBankCardDto.getIndentityType().name());
        buildSignParams.put("card_id", unBindingBankCardDto.getCardId());
        if (StringUtils.isNotBlank(unBindingBankCardDto.getUserUnBindIp()))
            buildSignParams.put("client_ip", unBindingBankCardDto.getUserUnBindIp());
        if (unBindingBankCardDto.getExtendParam() != null
            && !unBindingBankCardDto.getExtendParam().isEmpty())
            buildSignParams.put("extend_param",
                map2FormatStyle(unBindingBankCardDto.getExtendParam()));
    }

    @Override
    public ResultDto<List<BankCardEntry>> queryBankCards(QueryBankCardDto queryBankCardDto)
                                                                                           throws MemberGatewayInvokeFailureException {
        try {
            //字段校验
            Preconditions.checkNotNull(queryBankCardDto, "queryBankCardDto can not be null");
            checkFieldNotnull(queryBankCardDto);

            //请求的数据
            HashMap<String, String> requestMap = Maps.newHashMap();
            buildBaseParams(requestMap, Constants.SERVICE_QUERY_BANK_CARD, memberDefaultParterId,
                queryBankCardDto.getCharsetType(), queryBankCardDto.getMemo(),
                getRequestTimeString());
            buildQueryBankCard(requestMap, queryBankCardDto);

            //调用信息打点
                logger.info("查询银行卡[queryBankCards],请求参数为{}", requestMap);

            //请求结果处理
            return request(requestMap, queryBankCardDto.getCharsetType(), SignType.RSA,
                new JsonParser<List<BankCardEntry>>() {
                    @Override
                    public void parse(JSONObject json, ResultDto<List<BankCardEntry>> result,
                                      CharsetType charset, String jsonResponse)
                                                                               throws MemberGatewayInvokeFailureException {

                        if (json.containsKey("card_list") && !"null".equals(json.getString("card_list"))) {
                            String cardList = json.getString("card_list");

                            String[] items = StringUtils.split(cardList, Constants.VERTICAL_LINE);
                            List<BankCardEntry> entries = new ArrayList<BankCardEntry>(items.length);
                            for (String item : items) {
                                String[] values = StringUtils.split(item, Constants.ANGLE_BRACKETS);
                                Preconditions.checkArgument(
                                    values.length == CARD_LIST_CHECK_LENGTH,
                                    "card_list 条目长度不对，应该是%s个", CARD_LIST_CHECK_LENGTH);

                                BankCardEntry entry = new BankCardEntry(values[0], BankCode
                                    .valueOf(values[1]), values[2], values[3], (VerifyStatus
                                    .valueOf(values[6]) == VerifyStatus.Y ? true : false),
                                    values[7],(VerifyStatus
                                        .valueOf(values[8]) == VerifyStatus.Y ? true : false));
                                if (values[4].length() > 0)
                                    entry.setCardType(BankCardType.valueOf(values[4]));

                                if (values[5].length() > 0)
                                    entry.setCardAttr(BankServiceType.valueOf(values[5]));

                                entries.add(entry);
                            }
                            if (json.containsKey("total_item")) {
                                int size = json.getInteger("total_item");
                                if (size != entries.size())
                                    throw new InvalidFieldSizeException("total_item", cardList,
                                        size, entries.size());
                            }

                            // 查询结果成功，才验证签名
                            ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charset, rsaPublicSiginKey);

                            result.setModule(entries);
                            result.setTotalCount(entries.size());
                        } else
                            result.setModule(new ArrayList<BankCardEntry>());
                    }
                });

        } catch (Exception e) {
            throw new MemberGatewayInvokeFailureException(e);
        }
    }

    private static void buildQueryBankCard(Map<String, String> buildSignParams,
                                           QueryBankCardDto queryBankCardDto) {
        buildSignParams.put("identity_id", queryBankCardDto.getIdentityId());
        buildSignParams.put("identity_type", queryBankCardDto.getIdentityType().name());
        buildSignParams.put("card_id", queryBankCardDto.getCardId());
        if (queryBankCardDto.getExtendParam() != null
            && !queryBankCardDto.getExtendParam().isEmpty())
            buildSignParams.put("extend_param", map2FormatStyle(queryBankCardDto.getExtendParam()));
    }

    @Override
    public ResultDto<AccountBalance> queryBalance(QueryBalanceDto queryBalanceDto)
                                                                                  throws MemberGatewayInvokeFailureException {
        try {
            //字段校验
            Preconditions.checkNotNull(queryBalanceDto, "queryBalanceDto can not be null");
            checkFieldNotnull(queryBalanceDto);

            //请求的数据
            HashMap<String, String> requestMap = Maps.newHashMap();
            buildBaseParams(requestMap, Constants.SERVICE_QUERY_BALANCE, memberDefaultParterId,
                queryBalanceDto.getCharsetType(), queryBalanceDto.getMemo(), getRequestTimeString());
            buildQueryBalance(requestMap, queryBalanceDto);

              //调用信息打点
            logger.info("查询余额[queryBalance],请求参数为{}", requestMap);

            return request(requestMap, queryBalanceDto.getCharsetType(), SignType.RSA,
                new JsonParser<AccountBalance>() {
                    @Override
                    public void parse(JSONObject json, ResultDto<AccountBalance> result,
                                      CharsetType charset, String jsonResponse)
                                                                               throws MemberGatewayInvokeFailureException {
                        Preconditions.checkArgument(json.containsKey("balance"),
                            "member gateway lost field balance");
                        Preconditions.checkArgument(json.containsKey("available_balance"),
                            "member gateway lost field available_balance");
                        
                        logger.info("查询余额[queryBalance]响应{}",jsonResponse);
                        
                        // 查询结果成功，才验证签名
                        ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charset, rsaPublicSiginKey);
                       AccountBalance  accountBalance = new AccountBalance(new Money(json.getString("balance")), new Money(json.getString("available_balance")));
                       String earing = json.getString("bonus");
                     
                      //解析存钱罐的收益额
                      if(StringUtil.isNotBlank(earing)){
                    	  String[] values = StringUtils.split(earing, Constants.ANGLE_BRACKETS); 
                    	  if(org.apache.commons.lang3.math.NumberUtils.isNumber(values[0]))
                       	   accountBalance.setYestDayBonus(new Money(values[0]));                       
                          if(org.apache.commons.lang3.math.NumberUtils.isNumber(values[1]))                   
                       	   accountBalance.setLastMonthBonus(new Money(values[1]));
                          if(org.apache.commons.lang3.math.NumberUtils.isNumber(values[2]))
                       	   accountBalance.setBonus(new Money(values[2]));  
                      }                                            
                        logger.info("查询余额[queryBalance],结果{}", accountBalance.toString());
                        result.setModule(accountBalance);
                        result.setTotalCount(1);
                    }
                });

        } catch (Exception e) {
            throw new MemberGatewayInvokeFailureException(e);
        }
    }

    private static void buildQueryBalance(Map<String, String> buildSignParams,
                                          QueryBalanceDto queryBalanceDto) {
        buildSignParams.put("identity_id", queryBalanceDto.getIdentityId());
        buildSignParams.put("identity_type", queryBalanceDto.getIdentityType().name());
        buildSignParams.put("account_type", queryBalanceDto.getAccountType().name());
        if (queryBalanceDto.getExtendParam() != null && !queryBalanceDto.getExtendParam().isEmpty())
            buildSignParams.put("extend_param", map2FormatStyle(queryBalanceDto.getExtendParam()));
    }

    @Override
    public ResultDto<AccountDetail> queryAccountDetails(QueryAccountDetailsDto queryAccountDetailsDto)
                                                                                                      throws MemberGatewayInvokeFailureException {
        try {
            //字段校验
            Preconditions.checkNotNull(queryAccountDetailsDto,
                "queryAccountDetailsDto can not be null");
            checkFieldNotnull(queryAccountDetailsDto);

            //请求的数据
            HashMap<String, String> requestMap = Maps.newHashMap();
            buildBaseParams(requestMap, Constants.SERVICE_QUERY_ACCOUNT_DETAILS,
                memberDefaultParterId, queryAccountDetailsDto.getCharsetType(),
                queryAccountDetailsDto.getMemo(), getRequestTimeString());
            buildQueryAccountDetailsDto(requestMap, queryAccountDetailsDto);

            //调用信息打点
                logger.info("查询资金明细[queryAccountDetails],请求参数为{}", requestMap);

            return request(requestMap, queryAccountDetailsDto.getCharsetType(), SignType.RSA,
                new JsonParser<AccountDetail>() {
                    @Override
                    public void parse(JSONObject json, ResultDto<AccountDetail> result,
                                      CharsetType charset, String jsonResponse)
                                                                               throws MemberGatewayInvokeFailureException {
                        // 查询结果成功，才验证签名
                        ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charset, rsaPublicSiginKey);
                        AccountDetail accountDetail = new AccountDetail();

                        if (json.containsKey("total_income"))
                            accountDetail.setTotalIncome(json.getString("total_income"));

                        if (json.containsKey("total_payout"))
                            accountDetail.setTotalPayout(json.getString("total_payout"));

                        if (json.containsKey("total_item"))
                            result.setTotalCount(json.getInteger("total_item"));

                        List<StatementEntry> entries = new ArrayList<StatementEntry>();
                        if (json.containsKey("detail_list")) {
                            String[] items = StringUtils.split(json.getString("detail_list"),
                                Constants.VERTICAL_LINE);
                            for (String item : items) {
                                String[] values = StringUtils.split(item, Constants.ANGLE_BRACKETS);
                                if (values.length != 6)
                                    throw new InvalidFieldSizeException("detail_list", item, 5,
                                        values.length);
                                entries.add(new StatementEntry(values[0], values[1], values[2],
                                    new Money(values[3]), new Money(values[4]), values[5]));
                            }
                            accountDetail.setDetailList(entries);
                        }

                        result.setModule(accountDetail);
                    }
                });

        } catch (Exception e) {
            throw new MemberGatewayInvokeFailureException(e);
        }
    }

    private static void buildQueryAccountDetailsDto(Map<String, String> buildSignParams,
                                                    QueryAccountDetailsDto queryAccountDetailsDto) {
        buildSignParams.put("identity_id", queryAccountDetailsDto.getIdentityId());
        buildSignParams.put("identity_type", queryAccountDetailsDto.getIdentityType().name());
        if (queryAccountDetailsDto.getAccountType() != null)
            buildSignParams.put("account_type", queryAccountDetailsDto.getAccountType().name());
        buildSignParams.put("start_time", getTimeString(queryAccountDetailsDto.getStartTime()));
        buildSignParams.put("end_time", getTimeString(queryAccountDetailsDto.getEndTime()));

        if (StringUtils.isNotBlank(queryAccountDetailsDto.getPageNo()))
            buildSignParams.put("page_no", queryAccountDetailsDto.getPageNo());
        if (StringUtils.isNotBlank(queryAccountDetailsDto.getPageNo()))
            buildSignParams.put("page_size", queryAccountDetailsDto.getPageSize());

        if (queryAccountDetailsDto.getExtendParam() != null
            && !queryAccountDetailsDto.getExtendParam().isEmpty())
            buildSignParams.put("extend_param",
                map2FormatStyle(queryAccountDetailsDto.getExtendParam()));
    }

    @Override
    public ResultDto<?> freezeBalance(BalanceFreezeDto balanceFreezeDto)
                                                                        throws MemberGatewayInvokeFailureException {
        try {
            //字段校验
            Preconditions.checkNotNull(balanceFreezeDto, "balanceFreezeDto can not be null");
            checkFieldNotnull(balanceFreezeDto);

            //请求的数据
            HashMap<String, String> requestMap = Maps.newHashMap();
            buildBaseParams(requestMap, Constants.SERVICE_BALANCE_FREEZE, memberDefaultParterId,
                balanceFreezeDto.getCharsetType(), balanceFreezeDto.getMemo(),
                getRequestTimeString());
            buildBalanceFreezeDto(requestMap, balanceFreezeDto);

            //调用信息打点
            logger.info("冻结余额[freezeBalance],请求参数为{}", requestMap);


            return request(requestMap, SignType.RSA, balanceFreezeDto.getCharsetType());
        } catch (Exception e) {
            throw new MemberGatewayInvokeFailureException(e);
        }
    }

    private static void buildBalanceFreezeDto(Map<String, String> buildSignParams,
                                              BalanceFreezeDto balanceFreezeDto) {
        buildSignParams.put("out_freeze_no", balanceFreezeDto.getOutFreezeNo());
        buildSignParams.put("identity_id", balanceFreezeDto.getIdentityId());
        buildSignParams.put("identity_type", balanceFreezeDto.getIdentityType().name());
        if (balanceFreezeDto.getAccountType() != null)
            buildSignParams.put("account_type", balanceFreezeDto.getAccountType().name());
        buildSignParams.put("amount", balanceFreezeDto.getAmount().toString());
        buildSignParams.put("summary", balanceFreezeDto.getSummary());
        buildSignParams.put("client_ip", balanceFreezeDto.getClientIp());
        if (balanceFreezeDto.getExtendParam() != null
            && !balanceFreezeDto.getExtendParam().isEmpty())
            buildSignParams.put("extend_param", map2FormatStyle(balanceFreezeDto.getExtendParam()));
    }

    @Override
    public ResultDto<?> unfreezeBalance(BalanceUnFreezeDto balanceUnFreezeDto)
                                                                              throws MemberGatewayInvokeFailureException {

        try {
            //字段校验
            Preconditions.checkNotNull(balanceUnFreezeDto, "balanceUnFreezeDto can not be null");
            checkFieldNotnull(balanceUnFreezeDto);

            //请求的数据
            HashMap<String, String> requestMap = Maps.newHashMap();
            buildBaseParams(requestMap, Constants.SERVICE_BALANCE_UNFREEZE, memberDefaultParterId,
                balanceUnFreezeDto.getCharsetType(), balanceUnFreezeDto.getMemo(),
                getRequestTimeString());
            buildBalanceUnFreezeDto(requestMap, balanceUnFreezeDto);

            //解冻余额
            logger.info("冻结余额[unfreezeBalance],请求参数为{}", requestMap);

            return request(requestMap, SignType.RSA, balanceUnFreezeDto.getCharsetType());
        } catch (Exception e) {
            throw new MemberGatewayInvokeFailureException(e);
        }
    }

    private static void buildBalanceUnFreezeDto(Map<String, String> buildSignParams,
                                                BalanceUnFreezeDto balanceUnFreezeDto) {
        buildSignParams.put("out_unfreeze_no", balanceUnFreezeDto.getOutUnFreezeNo());
        buildSignParams.put("out_freeze_no", balanceUnFreezeDto.getOutFreezeNo());
        buildSignParams.put("identity_id", balanceUnFreezeDto.getIdentityId());
        buildSignParams.put("identity_type", balanceUnFreezeDto.getIdentityType().name());
        buildSignParams.put("summary", balanceUnFreezeDto.getSummary());
        buildSignParams.put("client_ip", balanceUnFreezeDto.getClientIp());
        buildSignParams.put("amount", balanceUnFreezeDto.getAmount().toString());
        if (balanceUnFreezeDto.getExtendParam() != null
            && !balanceUnFreezeDto.getExtendParam().isEmpty())
            buildSignParams.put("extend_param",
                map2FormatStyle(balanceUnFreezeDto.getExtendParam()));
    }

    private ResultDto<?> request(Map<String, String> params, SignType singType,
                                 CharsetType charsetType)
                                                         throws MemberGatewayInvokeFailureException {
        return request(params, charsetType, singType, null);
    }

    private interface JsonParser<T> {
        void parse(JSONObject json, ResultDto<T> result, CharsetType charset, String jsonResponse)
                                                                                                  throws MemberGatewayInvokeFailureException;
    }

    private <T> ResultDto<T> request(Map<String, String> params, CharsetType charsetType,
                                     SignType signType, JsonParser<T> cb)
                                                                         throws MemberGatewayInvokeFailureException {
        try {
            String jsonResponse = RequestUtil.buildRequest(memberGatewayUrl, params,
                SignType.RSA.name(), this.rsaPrivateSiginKey, memberDefaultSignVersion, charsetType.charset());

			logger.info("第三方支付返回结果：" + jsonResponse);
            JSONObject obj = JSONObject.parseObject(jsonResponse);
            checkGatewayResponse(obj);                

            boolean isSuccess = isSuccess(obj.getString("response_code"));
            ResultDto<T> result = new ResultDto<T>(isSuccess);
            if (isSuccess) {
                if (cb == null) {
                    ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charsetType, this.rsaPublicSiginKey);
                } else {
                    cb.parse(obj, result, charsetType, jsonResponse);
                }
                result.setSuccess(true);
            } else {
                result.setErrorCode(obj.getString("response_code"));
                result.setErrorMsg(obj.getString("response_message"));
            }
            return result;
        } catch (MemberGatewayInvokeFailureException e) {
            logger.error("调用微钱包会员网关失败。", e);
            throw e;
        } catch (Exception e) {
            logger.error("调用微钱包会员网关失败。", e);
            throw new MemberGatewayInvokeFailureException(e);
        }
    }

    private void checkGatewayResponse(JSONObject obj) {
        Preconditions.checkNotNull(obj, "网关无响应信息");
        Preconditions.checkArgument(obj.containsKey("response_code"), "返回结果中没有response_code");
    }

    private static final String     TIME_PATTERN = "yyyyMMddHHmmss";
    private static final DateFormat df           = new SimpleDateFormat(TIME_PATTERN);

    /**
     * 返回当前系统时间
     * @return yyyyMMddhhmmss
     */
    private static String getRequestTimeString() {
        return getTimeString(new Date());
    }

    /**
     * 
     * @return yyyyMMddhhmmss
     */
    private static String getTimeString(Date time) {
        return df.format(time);
    }

    /**
     * Map数据格式转化
     * @param extParamMap
     * @return
     */
    private static String map2FormatStyle(Map<String, String> extParamMap) {
        List<String> keyJoinValList = Lists.newArrayList();
        if (extParamMap != null && !extParamMap.isEmpty()) {
            for (Map.Entry<String, String> entry : extParamMap.entrySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append(CastStringUtil.underLineName(entry.getKey()));
                sb.append(Constants.ANGLE_BRACKETS);
                sb.append(entry.getValue());
                keyJoinValList.add(sb.toString());
            }
            return Joiner.on(Constants.VERTICAL_LINE).skipNulls().join(keyJoinValList);
        }
        return StringUtils.EMPTY;
    }

    private Map<String, String> buildBaseParams(Map<String, String> params,
                                                String defaultServiceName, String partnerId,
                                                CharsetType charsetType, String memo,
                                                String requestTime) {
        params.put("service", defaultServiceName);
        params.put("version", memberDefaultServerVersion);
        params.put("request_time", requestTime);
        params.put("partner_id", partnerId);
        params.put("_input_charset", charsetType.charset());
        params.put("encrypt_version", memberDefaultEncyptVersion);
        if (StringUtils.isNotBlank(memo))
            params.put("memo", memo);

        return params;
    }

    private boolean isSuccess(String responseCode) {
        return (ResponseCodeEnum.APPLY_SUCCESS.name().equals(responseCode) || ResponseCodeEnum.BIZ_PENDING
            .name().equals(responseCode)) ? true : false;
    }

    public void setMemberMd5KeyPath(String memberMd5KeyPath) {
        this.memberMd5KeyPath = memberMd5KeyPath;
    }

    public void setMemberPublicKey(String memberPublicKey) {
        this.memberPublicKey = memberPublicKey;
    }

    public void setMemberGatewayUrl(String memberGatewayUrl) {
        this.memberGatewayUrl = memberGatewayUrl;
    }

    public void setMemberDefaultSignVersion(String memberDefaultSignVersion) {
        this.memberDefaultSignVersion = memberDefaultSignVersion;
    }

    public void setMemberDefaultEncyptVersion(String memberDefaultEncyptVersion) {
        this.memberDefaultEncyptVersion = memberDefaultEncyptVersion;
    }

    public void setMemberDefaultServerVersion(String memberDefaultServerVersion) {
        this.memberDefaultServerVersion = memberDefaultServerVersion;
    }

    public void setMemberDefaultParterId(String memberDefaultParterId) {
        this.memberDefaultParterId = memberDefaultParterId;
    }

	public void setMemberMd5Key(String memberMd5Key) {
		this.memberMd5Key = memberMd5Key;
	}
	
	/**
	 * 绑定新浪用户
	 * @param bindingVerifyDto
	 * @return
	 * @throws MemberGatewayInvokeFailureException
	 */
	public ResultDto<?> bindingVerify(BindingVerifyDto bindingVerifyDto) throws MemberGatewayInvokeFailureException {
        try {
            //字段校验
            Preconditions.checkNotNull(bindingVerifyDto, "bindingVerifyDto can not be null");
            checkFieldNotnull(bindingVerifyDto);

            //请求的数据
            HashMap<String, String> requestMap = Maps.newHashMap();
            buildBaseParams(requestMap, Constants.SERVICE_BINDING_VERIFY, memberDefaultParterId,
                    bindingVerifyDto.getCharsetType(), bindingVerifyDto.getMemo(),
                    getRequestTimeString());
            buildBindingVerifyDto(requestMap, bindingVerifyDto);

            //解冻余额
            logger.info("绑定[bindingVerifyDto],请求参数为{}", requestMap);


            return request(requestMap, SignType.RSA, bindingVerifyDto.getCharsetType());
        } catch (Exception e) {
            throw new MemberGatewayInvokeFailureException(e);
        }

	}
	
	public ResultDto<?> unbindingVerify(BindingVerifyDto bindingVerifyDto) throws MemberGatewayInvokeFailureException {
        try {
            //字段校验
            Preconditions.checkNotNull(bindingVerifyDto, "bindingVerifyDto can not be null");
            //checkFieldNotnull(bindingVerifyDto);

            //请求的数据
            HashMap<String, String> requestMap = Maps.newHashMap();
            buildBaseParams(requestMap, Constants.SERVICE_UNBINDING_VERIFY, memberDefaultParterId,
                    bindingVerifyDto.getCharsetType(), bindingVerifyDto.getMemo(),
                    getRequestTimeString());
            unBuildBindingVerifyDto(requestMap, bindingVerifyDto);

            //解除绑定
            logger.info("解除绑定[bindingVerifyDto],请求参数为{}", requestMap);


            return request(requestMap, SignType.RSA, bindingVerifyDto.getCharsetType());
        } catch (Exception e) {
            throw new MemberGatewayInvokeFailureException(e);
        }

	}
    private   void  unBuildBindingVerifyDto(HashMap<String, String> requestMap,BindingVerifyDto  bindingVerifyDto) throws  Exception{
        requestMap.put("identity_id",bindingVerifyDto.getIdentityId());
        requestMap.put("identity_type",bindingVerifyDto.getIdentityType().name());
        requestMap.put("verify_type",bindingVerifyDto.getVerifyType().name());
       // requestMap.put("verify_entity",crytionByRSA(bindingVerifyDto.getVerifyEntity(),bindingVerifyDto.getCharsetType()));
        // 请求者IP
        if (StringUtil.isNotBlank(bindingVerifyDto.getClientIp())) {
        	requestMap.put("client_ip", bindingVerifyDto.getClientIp());
        }
        if (bindingVerifyDto.getExtendParam() != null
                && !bindingVerifyDto.getExtendParam().isEmpty())
            requestMap.put("extend_param",
                    map2FormatStyle(bindingVerifyDto.getExtendParam()));
    }
    private void buildBindingVerifyDto(HashMap<String, String> requestMap,BindingVerifyDto  bindingVerifyDto) throws  Exception{
        requestMap.put("identity_id",bindingVerifyDto.getIdentityId());
        requestMap.put("identity_type",bindingVerifyDto.getIdentityType().name());
        requestMap.put("verify_type",bindingVerifyDto.getVerifyType().name());
        requestMap.put("verify_entity",crytionByRSA(bindingVerifyDto.getVerifyEntity(),bindingVerifyDto.getCharsetType()));
        // 请求者IP
        if (StringUtil.isNotBlank(bindingVerifyDto.getClientIp())) {
        	requestMap.put("client_ip", bindingVerifyDto.getClientIp());
        }
        if (bindingVerifyDto.getExtendParam() != null
                && !bindingVerifyDto.getExtendParam().isEmpty())
            requestMap.put("extend_param",
                    map2FormatStyle(bindingVerifyDto.getExtendParam()));
    }

	public ResultDto<?> queryVerify(QueryVerifyDto queryVerifyDto)
			throws MemberGatewayInvokeFailureException {
		try {
			// 字段校验
			Preconditions.checkNotNull(queryVerifyDto,
					"bindingVerifyDto can not be null");
			checkFieldNotnull(queryVerifyDto);

			// 请求的数据
			HashMap<String, String> requestMap = Maps.newHashMap();
			buildBaseParams(requestMap, Constants.SERVICE_QUERY_VERIFY,
					memberDefaultParterId, queryVerifyDto.getCharsetType(),
					queryVerifyDto.getMemo(), getRequestTimeString());
			buildQueryVerifyDto(requestMap, queryVerifyDto);

			// 解冻余额
			logger.info("绑定[bindingVerifyDto],请求参数为{}", requestMap);


			return request(requestMap, SignType.RSA,queryVerifyDto.getCharsetType());
		} catch (Exception e) {
			throw new MemberGatewayInvokeFailureException(e);
		}
	}
	
	 private   void  buildQueryVerifyDto(HashMap<String, String> requestMap,QueryVerifyDto  queryVerifyDto) throws  Exception{
	        requestMap.put("identity_id",queryVerifyDto.getIdentityId());
	        requestMap.put("identity_type",queryVerifyDto.getIdentityType().name());
	        requestMap.put("verify_type",queryVerifyDto.getVerifyType().name());
	        requestMap.put("is_mask",queryVerifyDto.getIsMask());
	        if (queryVerifyDto.getExtendParam() != null
	                && !queryVerifyDto.getExtendParam().isEmpty())
	            requestMap.put("extend_param",
	                    map2FormatStyle(queryVerifyDto.getExtendParam()));
	 }

	@Override
	public String auditMemberInfos(CreateActiveMemberDto activeMemberDto) throws MemberGatewayInvokeFailureException {
		try {
            //字段校验
            Preconditions.checkNotNull(activeMemberDto, "activeMemberDto can not be null");
            checkFieldNotnull(activeMemberDto);
            //请求的数据
            TreeMap<String, String> requestMap = Maps.newTreeMap();
            buildBaseParams(requestMap, Constants.SHOW_MEMBER_INFOS_SINA,
                memberDefaultParterId, activeMemberDto.getCharsetType(), activeMemberDto.getMemo(),
                getRequestTimeString());
            buildActiveMemberParams(requestMap, activeMemberDto);
            //调用信息打点
            logger.info("新浪存钱罐查询验证[createActiveMember],请求参数为{}", requestMap);

            
            //生成加签名后的内容
            String charset = activeMemberDto.getCharsetType().charset();
            Map<String, String> requestIncludeSign = RequestUtil.buildRequestPara(requestMap,
                SignType.RSA.name(), rsaPrivateSiginKey, memberDefaultSignVersion, charset);
            // 创建表单
            Document doc = createAuditMemberInfosForm(requestIncludeSign);
            return doc.body().html();
            
        } catch (Exception e) {
            throw new MemberGatewayInvokeFailureException(e);
        }
	}

	 /**
     * 生成查询验证新浪存钱罐form
     * @param requestMap
     * @return
     */
    private Document createAuditMemberInfosForm(Map<String, String> requestMap) throws  Exception{
        // 生成<form></from>最外围结点
        Document doc = Jsoup.parseBodyFragment(com.yourong.common.thirdparty.sinapay.pay.core.common.Constants.FORM_ELEMENT_OUTER);

        // 获取form元素
        Element formElement = doc.select("form").first();
        formElement.attr("action", memberGatewayUrl);
        formElement.attr("method", com.yourong.common.thirdparty.sinapay.pay.core.common.Constants.FORM_REQEUST_METHOD);
        formElement.attr("id", "formAuthSavingPot");
        //生成input元素
        for (Map.Entry<String, String> entry : requestMap.entrySet()) {
            Element inputElement = doc.createElement("input");
            inputElement.attr("type", "hidden");
            inputElement.attr("name", entry.getKey());
            inputElement.attr("value", URLEncoder.encode(entry.getValue(), CharsetType.UTF8.name()));
            formElement.appendChild(inputElement);
        }
        return doc;
    }

 //************************************收营台接口**********************************/
	@Override
	public ResultDto<PayPasswordDto> handlePayPassword(PayPasswordDto payPasswordDto, String apiName) {
		ResultDto<PayPasswordDto> rDto = new ResultDto<PayPasswordDto>();
		try {
			// Dto 前置校验
			ResultDO<String> checkDO = ValueCheckUtil.valid(payPasswordDto);
			handleDtoError(rDto, checkDO, payPasswordDto, apiName);
			// 前置校验不通过返回
			if (checkDO.isError()) {
				return rDto;
			}
			// 封装请求的数据
			TreeMap<String, String> requestMap = Maps.newTreeMap();
			buildBaseParams(requestMap, apiName, memberDefaultParterId, payPasswordDto.getCharsetType(),
					payPasswordDto.getMemo(), getRequestTimeString());
			// TODO 可以考虑用注解将业务参数转map抽象出来
			requestMap.put("identity_id", payPasswordDto.getIdentityId());
			requestMap.put("identity_type", payPasswordDto.getIdentityType());
			if (StringUtil.isNotBlank(payPasswordDto.getReturnUrl())) {
				requestMap.put("return_url", payPasswordDto.getReturnUrl());
			}
			if (Constants.SET_PAY_PASSWORD.equals(apiName)) {
				requestMap.put("withhold_param", "withhold_auth_type^ACCOUNT");
			}
			// 调用信息打点
			logger.info("支付密码操作={},请求参数为{}", apiName, requestMap);
			// 结果处理
			return request(requestMap, payPasswordDto.getCharsetType(), SignType.RSA,
					new JsonParser<PayPasswordDto>() {
                @Override
                public void parse(JSONObject json, ResultDto<PayPasswordDto> result, CharsetType charset, String jsonResponse) throws MemberGatewayInvokeFailureException {
					PayPasswordDto returnUrlDto = new PayPasswordDto();
					if (StringUtil.isNotBlank(json.getString("redirect_url"))) {
						returnUrlDto.setRedirectUrl(json.getString("redirect_url"));
					}
					if (StringUtil.isNotBlank(json.getString("is_set_paypass"))) {
						returnUrlDto.setIsSetPaypass(json.getString("is_set_paypass"));
					}
					// 查询结果成功，才验证签名
					ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charset, rsaPublicSiginKey);
					result.setModule(returnUrlDto);
				}
			});
		} catch (Exception e) {
			logger.error("调用新浪支付密码操作失败 apiName={}, payPasswordDto={}", apiName, payPasswordDto, e);
			rDto.setErrorMsg(e.getMessage());
			rDto.setSuccess(false);
		}
		return rDto;
	}

	/**
	 * 
	 * @Description:前置校验处理
	 * @param rDto
	 *            调用者返回的对象
	 * @param checkDO
	 *            校验返回的DO
	 * @param dto
	 *            校验对象
	 * @param apiName
	 *            校验的接口名
	 * @author: wangyanji
	 * @time:2016年7月13日 下午4:45:26
	 */
	private void handleDtoError(ResultDto<?> rDto, ResultDO<String> checkDO, Object dto, String apiName) {
		// 前置校验通过且新浪接口名已经指定就视为可以通过校验，往下调用接口
		if (StringUtil.isBlank(apiName)) {
			checkDO.setSuccess(false);
			checkDO.setResult("新浪接口不能为空");
		}
		if (checkDO.isSuccess() && StringUtil.isNotBlank(apiName)) {
			return;
		}
		logger.error("提交新浪接口dto前置校验不通过, apiName={}, errorMsg={}, dto={}", apiName, checkDO.getResult(), dto);
		rDto.setSuccess(false);
		rDto.setErrorMsg(checkDO.getResult());
	}

	
	@Override
	public ResultDto<HandleWithholdAuthorityDto> handleWithholdAuthority(HandleWithholdAuthorityDto handleWithholdAuthorityDto, String apiName) 
			throws MemberGatewayInvokeFailureException {
		ResultDto<HandleWithholdAuthorityDto> rDto = new ResultDto<HandleWithholdAuthorityDto>();
		try {
			//字段校验,前置校验
			ResultDO<String> checkDO = ValueCheckUtil.valid(handleWithholdAuthorityDto);
			handleDtoError(rDto, checkDO, handleWithholdAuthorityDto, apiName);
			// 前置校验不通过返回
			if (checkDO.isError()) {
				return rDto;
			}
			// 封装请求的数据
			TreeMap<String, String> requestMap = Maps.newTreeMap();
			buildBaseParams(requestMap, apiName, memberDefaultParterId, handleWithholdAuthorityDto.getCharsetType(), handleWithholdAuthorityDto.getMemo(), getRequestTimeString());
			buildHandleWithholdAuthParam(requestMap, handleWithholdAuthorityDto, apiName);
			//调用信息打点
			logger.info("[委托扣款]操作"+ apiName +"],请求参数为{}", requestMap);
			// 结果处理
			return request(requestMap, handleWithholdAuthorityDto.getCharsetType(), SignType.RSA, new JsonParser<HandleWithholdAuthorityDto>() {
                @Override
                public void parse(JSONObject json, ResultDto<HandleWithholdAuthorityDto> result, CharsetType charset, String jsonResponse) throws MemberGatewayInvokeFailureException {
                	HandleWithholdAuthorityDto returnUrlDto = new HandleWithholdAuthorityDto();
					if (StringUtil.isNotBlank(json.getString("redirect_url"))) {
						returnUrlDto.setRedirectUrl(json.getString("redirect_url"));
					}
					// 查询结果成功，才验证签名
					ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charset, rsaPublicSiginKey);
					result.setSuccess(true);
					result.setModule(returnUrlDto);
				}
			});
			
		} catch (Exception e) {
			logger.error("[委托扣款]操作调用api"+ apiName +"失败， handleWithholdAuthorityDto={}", handleWithholdAuthorityDto, e);
			rDto.setErrorMsg(e.getMessage());
			rDto.setSuccess(false);
		}
		return rDto;
	}
	
	// 组装委托扣款参数
	private void buildHandleWithholdAuthParam(Map<String, String> buildSignParams, HandleWithholdAuthorityDto handleWithholdAuthorityDto, String apiName) throws Exception {
		buildSignParams.put("identity_id", handleWithholdAuthorityDto.getIdentityId());
		buildSignParams.put("identity_type", handleWithholdAuthorityDto.getIdentityType().name());
		buildSignParams.put("return_url", handleWithholdAuthorityDto.getReturnUrl());
		if (Constants.HANDLE_WITHHOLD_AUTHORITY.equals(apiName)) {
			buildSignParams.put("quota", handleWithholdAuthorityDto.getQuota());
			buildSignParams.put("day_quota", handleWithholdAuthorityDto.getDayQuota());
			buildSignParams.put("auth_type_whitelist", handleWithholdAuthorityDto.getAuthTypeWhitelist().name());
		} else if (Constants.QUERY_WITHHOLD_AUTHORITY.equals(apiName)) {
			if (handleWithholdAuthorityDto.getAuthTypeWhitelist() != null && !handleWithholdAuthorityDto.getAuthTypeWhitelist().name().isEmpty()) {
				buildSignParams.put("auth_type", handleWithholdAuthorityDto.getAuthTypeWhitelist().name());
			}
			if (StringUtils.isNotEmpty(handleWithholdAuthorityDto.getIsDetailDisp())) {
				buildSignParams.put("is_detail_disp", handleWithholdAuthorityDto.getIsDetailDisp());
			}
		}
		if (handleWithholdAuthorityDto.getExtendParam() != null && !handleWithholdAuthorityDto.getExtendParam().isEmpty()) {
			buildSignParams.put("extend_param", map2FormatStyle(handleWithholdAuthorityDto.getExtendParam()));
		}
	}

	@Override
	public ResultDto<HandleWithholdAuthorityDto> queryWithholdAuthority(HandleWithholdAuthorityDto handleWithholdAuthorityDto)
			throws MemberGatewayInvokeFailureException {
		ResultDto<HandleWithholdAuthorityDto> rDto = new ResultDto<HandleWithholdAuthorityDto>();
		try {
			//字段校验,前置校验
			ResultDO<String> checkDO = ValueCheckUtil.valid(handleWithholdAuthorityDto);
			handleDtoError(rDto, checkDO, handleWithholdAuthorityDto, Constants.QUERY_WITHHOLD_AUTHORITY);
			// 前置校验不通过返回
			if (checkDO.isError()) {
				return rDto;
			}
			// 封装请求的数据
			TreeMap<String, String> requestMap = Maps.newTreeMap();
			buildBaseParams(requestMap, Constants.QUERY_WITHHOLD_AUTHORITY, memberDefaultParterId, handleWithholdAuthorityDto.getCharsetType(), handleWithholdAuthorityDto.getMemo(), getRequestTimeString());
			buildHandleWithholdAuthParam(requestMap, handleWithholdAuthorityDto, Constants.QUERY_WITHHOLD_AUTHORITY);
			//调用信息打点
			logger.info("查看用户是否委托扣款],请求参数为{}", requestMap);
			// 结果处理
			return request(requestMap, handleWithholdAuthorityDto.getCharsetType(), SignType.RSA, new JsonParser<HandleWithholdAuthorityDto>() {
                @Override
                public void parse(JSONObject json, ResultDto<HandleWithholdAuthorityDto> result, CharsetType charset, String jsonResponse) throws MemberGatewayInvokeFailureException {
                	HandleWithholdAuthorityDto returnUrlDto = new HandleWithholdAuthorityDto();
					if (StringUtil.isNotBlank(json.getString("is_withhold_authoity"))) {
						returnUrlDto.setIsWithholdAuthoity(json.getString("is_withhold_authoity"));
					}
					if (StringUtil.isNotBlank(json.getString("withhold_authoity_time"))) {
						returnUrlDto.setWithholdAuthoityTime(json.getString("withhold_authoity_time"));
					}
					
					// 查询结果成功，才验证签名
					ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charset, rsaPublicSiginKey);
					result.setSuccess(true);
					result.setModule(returnUrlDto);
				}
			});
			
		} catch (Exception e) {
			logger.error("查看用户是否委托扣款失败， handleWithholdAuthorityDto={}", handleWithholdAuthorityDto, e);
			rDto.setErrorMsg(e.getMessage());
			rDto.setSuccess(false);
		}
		return rDto;
	}
	/**
	 * 
	 * @Description:查询冻结、解冻结果
	 * @param queryCtrlResultDto
	 * @return
	 * @throws MemberGatewayInvokeFailureException
	 * @author: chaisen
	 * @time:2016年7月27日 下午5:43:26
	 */
	public ResultDto<QueryCtrlResultDto> queryCtrlResult(QueryCtrlResultDto queryCtrlResultDto)
			throws MemberGatewayInvokeFailureException {
		ResultDto<QueryCtrlResultDto> rDto = new ResultDto<QueryCtrlResultDto>();
		try {
			// 字段校验
			Preconditions.checkNotNull(queryCtrlResultDto,
					"queryCtrlResultDto can not be null");
			checkFieldNotnull(queryCtrlResultDto);

			// 请求的数据
			HashMap<String, String> requestMap = Maps.newHashMap();
			buildBaseParams(requestMap, Constants.QUERY_CTRL_RESULT,
					memberDefaultParterId, queryCtrlResultDto.getCharsetType(),
					queryCtrlResultDto.getMemo(), getRequestTimeString());
			buildQueryCtrlResultDto(requestMap, queryCtrlResultDto);
			logger.info("查询冻结、解冻结果[queryCtrlResultDto],请求参数为{}", requestMap);
			// 结果处理
						return request(requestMap, queryCtrlResultDto.getCharsetType(), SignType.RSA, new JsonParser<QueryCtrlResultDto>() {
			                @Override
			                public void parse(JSONObject json, ResultDto<QueryCtrlResultDto> result, CharsetType charset, String jsonResponse) throws MemberGatewayInvokeFailureException {
			                	QueryCtrlResultDto returnDto = new QueryCtrlResultDto();
								if (StringUtil.isNotBlank(json.getString("ctrl_status"))) {
									returnDto.setCtrlStatus(json.getString("ctrl_status"));
								}
								if (StringUtil.isNotBlank(json.getString("error_msg"))) {
									returnDto.setErrorMsg(json.getString("error_msg"));
								}
								if (StringUtil.isNotBlank(json.getString("out_ctrl_no"))) {
									returnDto.setOutCtrlNo(json.getString("out_ctrl_no"));
								}
								result.setSuccess(true);
								result.setModule(returnDto);
							}
						});
		} catch (Exception e) {
			logger.error("查冻结和解冻结果失败， queryCtrlResultDto={}", queryCtrlResultDto, e);
			rDto.setErrorMsg(e.getMessage());
			rDto.setSuccess(false);
		}
		return rDto;
	}
	
	 private   void  buildQueryCtrlResultDto(HashMap<String, String> requestMap,QueryCtrlResultDto  queryCtrlResultDto) throws  Exception{
	        requestMap.put("out_ctrl_no",queryCtrlResultDto.getOutCtrlNo());
	        if (queryCtrlResultDto.getExtendParam() != null
	                && !queryCtrlResultDto.getExtendParam().isEmpty())
	            requestMap.put("extend_param",
	                    map2FormatStyle(queryCtrlResultDto.getExtendParam()));
	 }
	 
	 @Override
		public ResultDto<Object> showMemberInfosSina(Long memberId,	String returnUrl) {
			ResultDto<Object> rDto = new ResultDto<Object>();
			
			
			// 封装请求的数据
				TreeMap<String, String> requestMap = Maps.newTreeMap();
				String apiName=Constants.SHOW_MEMBER_INFOS_SINA;
				requestMap.put("identity_id", SerialNumberUtil.generateIdentityId(memberId));
				requestMap.put("identity_type", IdType.UID.toString());
				requestMap.put("default_page", "DEFAULT");
				requestMap.put("templet_custom", "1001");
				requestMap.put("single_custom", "ACCOUNT.account_list^SAVING_POT|WITHHOLD.auth_type_whitelist^ACCOUNT");
				requestMap.put("resp_method", "1");
				//buildRequestByRSA();
				if(StringUtil.isNotBlank(returnUrl)){
					requestMap.put("return_url", returnUrl);
				}
			
				buildBaseParams(requestMap, apiName, memberDefaultParterId, CharsetType.UTF8,
								"", getRequestTimeString());
				// TODO 可以考虑用注解将业务参数转map抽象出来
						
				// 调用信息打点
				logger.info("sina页面展示用户信息={},请求参数为{}", apiName, requestMap);
				// 结果处理
				String jsonResponse=null;
				  try {
					  jsonResponse = RequestUtil.buildRequest(memberGatewayUrl, requestMap,
					            SignType.RSA.name(), this.rsaPrivateSiginKey, memberDefaultSignVersion, CharsetType.UTF8.charset());
				  	} catch (Exception e) {
				  		logger.error("调用新浪用户信息新浪接口异常， memberId={}", memberId, e);
					}
				  JSONObject  jasonObject = JSONObject.parseObject(jsonResponse);
				  Map map = (Map)jasonObject;	
				  
				 if(SinaPayEnum.RESPONSE_APPLY_SUCCESS.getCode().equals(map.get("response_code"))){
					 rDto.setModule(map.get("redirect_url"));
					 rDto.setSuccess(true);
				 }else{
					 logger.info("调用新浪用户信息新浪接口异常， 返回结果map={}",map);
					 rDto.setSuccess(false);
					 rDto.setErrorCode(SinaPayEnum.getEnumByCode(map.get("response_code").toString()).getCode());
					 rDto.setErrorMsg(SinaPayEnum.getEnumByCode(map.get("response_code").toString()).getDesc());
					 
				 }
			return rDto;
		}
		 
}
