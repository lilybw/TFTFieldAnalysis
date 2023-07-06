package gbw.riot.tftfieldanalysis;

import java.util.List;

public record MatchData(Metadata metadata, Info info) {
    public record Metadata(String data_version, String match_id, List<String> participants) {}

    public record Info(long game_datetime, double game_length, String game_version, List<Participant> participants) {
        public record Participant(List<String> augments, Companion companion, int gold_left, int last_round, int level,
                                  int placement, int players_eliminated, String puuid, double time_eliminated,
                                  int total_damage_to_players, List<Trait> traits, List<Unit> units) {}

        public record Companion(String content_ID, int item_ID, int skin_ID, String species) {}

        public record Trait(String name, int num_units, int style, int tier_current, int tier_total) {}

        public record Unit(String character_id, List<String> itemNames, String name, int rarity, int tier) {}
    }
}
