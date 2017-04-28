package com.yourong.core.uc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.yourong.common.pageable.Page;
import com.yourong.core.uc.model.MemberBankCard;

@Repository
public interface MemberBankCardMapper {
    @Delete({
            "delete from uc_member_bank_card",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
            "insert into uc_member_bank_card (id, member_id, ",
            "card_number, card_holder, ",
            "bank_code, bank_province, ",
            "bank_city, bank_district, ",
            "branch_name, sub_branch_name, ",
            "is_default, create_time, ",
            "update_time, del_flag, ",
            "remarks,card_Type,bank_Mobile,is_Security)",
            "values (#{id,jdbcType=BIGINT}, #{memberId,jdbcType=BIGINT}, ",
            "#{cardNumber,jdbcType=VARCHAR}, #{cardHolder,jdbcType=VARCHAR}, ",
            "#{bankCode,jdbcType=VARCHAR}, #{bankProvince,jdbcType=VARCHAR}, ",
            "#{bankCity,jdbcType=VARCHAR}, #{bankDistrict,jdbcType=VARCHAR}, ",
            "#{branchName,jdbcType=VARCHAR}, #{subBranchName,jdbcType=VARCHAR}, ",
            "#{isDefault,jdbcType=INTEGER}, #{createTime,jdbcType=TIMESTAMP}, ",
            "#{updateTime,jdbcType=TIMESTAMP}, #{delFlag,jdbcType=INTEGER}, ",
            "#{remarks,jdbcType=VARCHAR}),",
            "#{cardType,jdbcType=INTEGER}, #{bankMobile,jdbcType=BIGINT},#{isSecurity,jdbcType=INTEGER}",
    })
    int insert(MemberBankCard record);

    int insertSelective(MemberBankCard record);

    @Select({
            "select",
            "id, member_id, card_number, card_holder, bank_code, bank_province, bank_city, ",
            "bank_district, branch_name, sub_branch_name, is_default, create_time, update_time, ",
            "del_flag, remarks,outer_source_id,card_Type,bank_Mobile,is_Security ",
            "from uc_member_bank_card",
            "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    MemberBankCard selectByPrimaryKey(Long id);

    @Select({
            "select",
            "id, member_id, card_number, card_holder, bank_code, bank_province, bank_city, ",
            "bank_district, branch_name, sub_branch_name, is_default, create_time, update_time, ",
            "del_flag, remarks,outer_source_id,card_Type,bank_Mobile ,is_Security",
            "from uc_member_bank_card",
            "where id = #{id,jdbcType=BIGINT} and del_flag=-1 "
    })
    @ResultMap("BaseResultMap")
    MemberBankCard selectDeletedBankCard(Long id);

    @Select({
            "select",
            " 1 ",
            "from uc_member_bank_card",
            "where id = #{id,jdbcType=BIGINT} and member_id= #{memberId,jdbcType=BIGINT}"
    })
    Integer isExist(@Param("id") Long id, @Param("memberId") Long memberID);


    @Select({
            "select",
            "id, member_id, card_number, card_holder, bank_code, bank_province, bank_city, ",
            "bank_district, branch_name, sub_branch_name, is_default, create_time, update_time, ",
            "del_flag, remarks,outer_source_id,card_Type,bank_Mobile,is_Security, locate('*', card_number) as isSinaAdded ",
            "from uc_member_bank_card",
            "where member_id = #{memberId,jdbcType=BIGINT}  and del_flag > 0 ",
            "and ((card_Type=2 and locate('*', card_number) > 0) or locate('*', card_number) = 0) ",
            "order by create_time desc "
    })
    @ResultMap("BaseResultMap")
    List<MemberBankCard> selectByMemberID(Long memberId);


    int updateByPrimaryKeySelective(MemberBankCard record);

    @Update({
            "update uc_member_bank_card",
            "set member_id = #{memberId,jdbcType=BIGINT},",
            "card_number = #{cardNumber,jdbcType=VARCHAR},",
            "card_holder = #{cardHolder,jdbcType=VARCHAR},",
            "bank_code = #{bankCode,jdbcType=VARCHAR},",
            "bank_province = #{bankProvince,jdbcType=VARCHAR},",
            "bank_city = #{bankCity,jdbcType=VARCHAR},",
            "bank_district = #{bankDistrict,jdbcType=VARCHAR},",
            "branch_name = #{branchName,jdbcType=VARCHAR},",
            "sub_branch_name = #{subBranchName,jdbcType=VARCHAR},",
            "is_default = #{isDefault,jdbcType=INTEGER},",
            "create_time = #{createTime,jdbcType=TIMESTAMP},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP},",
            "del_flag = #{delFlag,jdbcType=INTEGER},",
            "remarks = #{remarks,jdbcType=VARCHAR}",
            "card_Type = #{cardType,jdbcType=INTEGER}",
            "is_Security = #{isSecurity,jdbcType=INTEGER}",
            "bank_Mobile = #{bankMobile,jdbcType=BIGINT}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(MemberBankCard record);

    Page<MemberBankCard> findByPage(Page<MemberBankCard> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);

    @Select({
            "select",
            " id, member_id, card_number, card_holder, bank_code, bank_province, bank_city, ",
            " bank_district, branch_name, sub_branch_name, is_default, create_time, update_time, card_Type,bank_Mobile,is_Security ",
            " from uc_member_bank_card",
            " where card_number = #{cardNumber,jdbcType=VARCHAR} and member_id=#{memberId,jdbcType=BIGINT}",
            " and del_flag >= 0   "
    })
    MemberBankCard getMemberBankCardByCardNumberAndMemberId(@Param("cardNumber") String cardNumber, @Param("memberId") Long memberId);


    @Update({
            "update uc_member_bank_card ",
            " set " ,
            " bank_Mobile = #{bankMobile,jdbcType=BIGINT},",
            " card_Type = #{cardType,jdbcType=INTEGER},",
            " outer_source_id = #{outerSourceId,jdbcType=VARCHAR},",
            " update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT} and card_Type = 1"
    })
    int updateMemberBankCardQuickPay(MemberBankCard record);


    @Update({
            "update uc_member_bank_card ",
            " set is_default = 0 where member_id = #{memberId,jdbcType=BIGINT} and is_default > 0"
    })
    int resetMemberBankCardDefaultStatus(Long memberId);

    @Update({
            "update uc_member_bank_card ",
            " set is_Security = 1 where id = #{id,jdbcType=BIGINT} and del_flag = 1"
    })
    int updateMemberBankCardSecurity(Long id);

    @Update({
            "update uc_member_bank_card ",
            " set is_default = 1 where id = #{id,jdbcType=BIGINT} and is_default > 0"
    })
    int setDefaultMemberBankCard(Long id);


    Long getMemberBankCardQuantityByMemberId(Long memberId);

    @Update({
            "update uc_member_bank_card ",
            " set del_flag = -1, is_default = 0, user_unbind_ip = #{userUnbindIp,jdbcType=VARCHAR} where id = #{id,jdbcType=BIGINT} and del_flag = 1"
    })
    int deleteMemberBankCard(@Param("id") Long id, @Param("userUnbindIp") String userUnbindIp);

    /* 获取默认银行卡，如果没有默认获取第一张  */
    MemberBankCard getDefaultBankCardByMemberId(Long memberId);

    /**
     * 获取用户的所有的快捷支付
     * @param memberID
     * @return
     */
    List<MemberBankCard>  selectAllQuickPayBankCard(Long memberID);


    /**
     * 所有的快捷支付卡,不是安全卡
     *
     * @return
     */
    @Select({
            "select",
            "id, member_id, card_number, card_holder, bank_code, bank_province, bank_city, ",
            "bank_district, branch_name, sub_branch_name, is_default, create_time, update_time, ",
            "del_flag, remarks,outer_source_id,card_Type,bank_Mobile,is_Security ",
            "from uc_member_bank_card",
            "where  card_Type = 2 and  is_Security = 0  and del_flag > 0  "
    })
    List<MemberBankCard>  selectAllQuickPayNotSecurity();
    
    /**
     * 获得用户安全快捷支付卡信息
     * @param memberId
     * @return
     */
    MemberBankCard querySecurityBankCard(Long memberId);

    /**
     * 获取用户非安全卡的银行卡
     * @param memberId
     * @return
     */
    List<MemberBankCard> selectNonSecurityBankCard(Long memberId);
    
    /**
     * 获取用户银行卡信息
     * @param id
     * @param memberId
     * @return
     */
    MemberBankCard getMemberBankCardById(@Param("id")Long id, @Param("memberId")Long memberId);
    
    /**
     * 银行卡管理
     * @param pageRequest
     * @param map
     * @return
     */
    List<MemberBankCard> queryMemberCard(@Param("map") Map<String, Object> map);
    
    int queryMemberCardCount(@Param("map") Map<String, Object> map);
    
    /**
     * 根据外部sourceId查询会员银行卡
     * 
     * @param memberId
     * @param outerSourceId
     * @return
     */
    MemberBankCard getMemberCardByOuterSource(@Param("memberId")Long memberId, @Param("outerSourceId")String outerSourceId);
    
}