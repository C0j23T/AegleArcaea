package moe.hepta.arcaea.beans;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
public class RawSongList implements Serializable {
    @Serial
    private static final long serialVersionUID = 114514L;

    @SerializedName("status")
    private Integer status;
    @SerializedName("content")
    private ContentDTO content;

    @NoArgsConstructor
    @Data
    public static class ContentDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 114516L;

        @SerializedName("songs")
        private List<SongsDTO> songs;

        @NoArgsConstructor
        @Data
        public static class SongsDTO implements Serializable {
            @Serial
            private static final long serialVersionUID = 114517L;

            @SerializedName("song_id")
            private String songId;
            @SerializedName("difficulties")
            private List<DifficultiesDTO> difficulties;
            @SerializedName("alias")
            private List<String> alias;

            @NoArgsConstructor
            @Data
            public static class DifficultiesDTO implements Serializable {
                @Serial
                private static final long serialVersionUID = 114518L;

                @SerializedName("name_en")
                private String nameEn;
                @SerializedName("name_jp")
                private String nameJp;
                @SerializedName("artist")
                private String artist;
                @SerializedName("bpm")
                private String bpm;
                @SerializedName("bpm_base")
                private Double bpmBase;
                @SerializedName("set")
                private String set;
                @SerializedName("set_friendly")
                private String setFriendly;
                @SerializedName("time")
                private Integer time;
                @SerializedName("side")
                private Integer side;
                @SerializedName("world_unlock")
                private Boolean worldUnlock;
                @SerializedName("remote_download")
                private Boolean remoteDownload;
                @SerializedName("bg")
                private String bg;
                @SerializedName("date")
                private Integer date;
                @SerializedName("version")
                private String version;
                @SerializedName("difficulty")
                private Integer difficulty;
                @SerializedName("rating")
                private Integer rating;
                @SerializedName("note")
                private Integer note;
                @SerializedName("chart_designer")
                private String chartDesigner;
                @SerializedName("jacket_designer")
                private String jacketDesigner;
                @SerializedName("jacket_override")
                private Boolean jacketOverride;
                @SerializedName("audio_override")
                private Boolean audioOverride;
            }
        }
    }
}
