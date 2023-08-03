package com.day.cabbage.pojo.osu;

import cn.hutool.core.annotation.Alias;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;

@Data
public class Beatmap {
    //提供兼容osu search的API
    @Alias("beatmapset_id")
    @SerializedName("beatmapset_id")
    private Integer beatmapSetId;
    @Alias("beatmap_id")
    @SerializedName("beatmap_id")
    private Integer beatmapId;
    @Alias("approved")
    @SerializedName(value = "beatmap_status", alternate = {"approved"})
    private Integer approved;
    @Alias("total_length")
    @SerializedName("total_length")
    private Integer totalLength;
    @Alias("hit_length")
    @SerializedName(value = "play_length", alternate = {"hit_length"})
    private Integer hitLength;
    @Alias("version")
    @SerializedName(value = "difficulty_name", alternate = {"version"})
    private String version;
    @Alias("file_md5")
    @SerializedName("file_md5")
    private String fileMd5;
    @Alias("diff_size")
    @SerializedName(value = "difficulty_cs", alternate = {"diff_size"})
    private Float diffSize;
    @Alias("diff_overall")
    @SerializedName(value = "difficulty_od", alternate = {"diff_overall"})
    private Float diffOverall;
    @Alias("diff_approach")
    @SerializedName(value = "difficulty_ar", alternate = {"diff_approach"})
    private Float diffApproach;
    @Alias("diff_drain")
    @SerializedName(value = "difficulty_hp", alternate = {"diff_drain"})
    private Float diffDrain;

    @SerializedName(value = "gamemode", alternate = {"mode"})
    private Integer mode;
    @Alias("approved_date")
    @SerializedName(value = "date", alternate = {"approved_date"})
    private Date approvedDate;
    @Alias("last_update")
    @SerializedName("last_update")
    private Date lastUpdate;

    private String artist;

    private String title;
    @SerializedName(value = "mapper", alternate = {"creator"})
    private String creator;

    private Double bpm;

    private String source;

    private String tags;

    @Alias("genre_id")
    private Integer genreId;

    @Alias("language_id")
    private Integer languageId;
    @Alias("favourite_count")
    @SerializedName(value = "favorites", alternate = {"favourite_count"})
    private Integer favouriteCount;
    @Alias("playcount")
    @SerializedName("playcount")
    private Long playCount;
    @Alias("passcount")
    @SerializedName("passcount")
    private Long passCount;
    @Alias("max_combo")
    @SerializedName("max_combo")
    private Integer maxCombo;
    @Alias("difficulty")
    @SerializedName(value = "difficultyrating", alternate = {"difficulty"})
    private Double difficultyRating;
    @Alias("artist_unicode")
    @SerializedName("artist_unicode")
    private String artistUnicode;
    @Alias("title_unicode")
    @SerializedName("title_unicode")
    private String titleUnicode;
}

