package org.thingsboard.server.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 金蝶接口返回参数实体类
 * @author lhj
 */
@Data
public class KingdeeInterfaceParticipate  implements Serializable {

    private static final long serialVersionUID = 1677577226134853483L;
    private int ErrorCode;
    private boolean IsSuccess;
    private List<Errors> errors;
    private List<SuccessEntitys> successEntitys;
    private List<SuccessMessages> successMessages;

    @Data
    public static class Errors {
        private String FieldName;
        private String Message;
        private String DIndex;
    }

    @Data
    public static class SuccessEntitys {
        private String Id;
        private String Number;
        private String DIndex;
    }

    @Data
    public static class SuccessMessages {
        private String FieldName;
        private String Message;
        private String DIndex;
    }

}



