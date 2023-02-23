package moe.hepta.arcaea.beans;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class B30Bean {
    @SerializedName("status")
    private Integer status;
    @SerializedName("content")
    private ContentDTO content;

    @NoArgsConstructor
    @Data
    public static class ContentDTO {
        @SerializedName("best30_avg")
        private Double best30Avg;
        @SerializedName("recent10_avg")
        private Double recent10Avg;
        @SerializedName("account_info")
        private AccountInfoDTO accountInfo;
        @SerializedName("best30_list")
        private List<Best30ListDTO> best30List;
        @SerializedName("best30_overflow")
        private List<Best30OverflowDTO> best30Overflow;

        @NoArgsConstructor
        @Data
        public static class AccountInfoDTO {
            @SerializedName("code")
            private String code;
            @SerializedName("name")
            private String name;
            @SerializedName("user_id")
            private Integer userId;
            @SerializedName("is_mutual")
            private Boolean isMutual;
            @SerializedName("is_char_uncapped_override")
            private Boolean isCharUncappedOverride;
            @SerializedName("is_char_uncapped")
            private Boolean isCharUncapped;
            @SerializedName("is_skill_sealed")
            private Boolean isSkillSealed;
            @SerializedName("rating")
            private Short rating;
            @SerializedName("join_date")
            private Long joinDate;
            @SerializedName("character")
            private Short character;
        }

        @NoArgsConstructor
        @Data
        public static class Best30ListDTO {
            @SerializedName("score")
            private Integer score;
            @SerializedName("health")
            private Integer health;
            @SerializedName("rating")
            private Double rating;
            @SerializedName("song_id")
            private String songId;
            @SerializedName("modifier")
            private Integer modifier;
            @SerializedName("difficulty")
            private Integer difficulty;
            @SerializedName("clear_type")
            private Integer clearType;
            @SerializedName("best_clear_type")
            private Integer bestClearType;
            @SerializedName("time_played")
            private Long timePlayed;
            @SerializedName("near_count")
            private Integer nearCount;
            @SerializedName("miss_count")
            private Integer missCount;
            @SerializedName("perfect_count")
            private Integer perfectCount;
            @SerializedName("shiny_perfect_count")
            private Integer shinyPerfectCount;
        }

        @NoArgsConstructor
        @Data
        public static class Best30OverflowDTO {
            @SerializedName("score")
            private Integer score;
            @SerializedName("health")
            private Integer health;
            @SerializedName("rating")
            private Double rating;
            @SerializedName("song_id")
            private String songId;
            @SerializedName("modifier")
            private Integer modifier;
            @SerializedName("difficulty")
            private Integer difficulty;
            @SerializedName("clear_type")
            private Integer clearType;
            @SerializedName("best_clear_type")
            private Integer bestClearType;
            @SerializedName("time_played")
            private Long timePlayed;
            @SerializedName("near_count")
            private Integer nearCount;
            @SerializedName("miss_count")
            private Integer missCount;
            @SerializedName("perfect_count")
            private Integer perfectCount;
            @SerializedName("shiny_perfect_count")
            private Integer shinyPerfectCount;
        }
    }
}
