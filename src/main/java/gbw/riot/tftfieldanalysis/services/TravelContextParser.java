package gbw.riot.tftfieldanalysis.services;

import gbw.riot.tftfieldanalysis.core.ValueErrorTuple;
import gbw.riot.tftfieldanalysis.core.travel.BranchEntry;
import gbw.riot.tftfieldanalysis.responseUtil.ArrayUtil;
import gbw.riot.tftfieldanalysis.responseUtil.IntUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * To allow for easy reproduction of travel tree, this custom parser is used.
 * Break it. I dare you.
 * Break: cause an exception to be THROWN.
 */
@Service
public class TravelContextParser {

    @Autowired
    private SecretsService secrets;

    //duely note that these values are updated before each parsing
    public String entryEnd = "_",
            entryMemberSubdivision = ",",
            entrySubdivision = "'";

    public TravelContextParser(){}
    //for testing purposes
    public TravelContextParser(SecretsService secrets){
        this.secrets = secrets;
    }


    public ValueErrorTuple<List<BranchEntry>,Exception> parseParam(String param){
        ValueErrorTuple<String[],Exception> preflightResult = preflightChecks(param);
        if(preflightResult.hasError())
            return ValueErrorTuple.error(preflightResult.error());

        String[] entries = preflightResult.value();
        List<BranchEntry> toReturn = new ArrayList<>();

        for(int i = 0 ; i < entries.length ; i++){
            ValueErrorTuple<BranchEntry,Exception> parsed = parseEntry(entries[i]);
            if(parsed.hasError()){
                return ValueErrorTuple.error(
                        new Exception(
                                parsed.error().getMessage()
                                + "\nOccurred in entry #" + i
                        )
                );
            }
            toReturn.add(parsed.value());
        }

        return ValueErrorTuple.value(toReturn);
    }

    private ValueErrorTuple<BranchEntry,Exception> parseEntry(String entry){
        String[] members = entry.split(entrySubdivision);
        if(members.length != 4)
            return ValueErrorTuple.error(new Exception(
                    "Incorrect amount of entry members"
            ));

        int start = IntUtil.parseOr(members[0],-1);
        String[] includedNamespaces = trimResizeAndNullOnEmpty(members[1]);
        String[] includedTags = trimResizeAndNullOnEmpty(members[2]);
        String[] preprocessedIds = trimResizeAndNullOnEmpty(members[3]);
        int[] pointIds = null;

        if(preprocessedIds != null && preprocessedIds.length > 0){
            pointIds = new int[preprocessedIds.length];
            for(int i = 0; i < pointIds.length; i++){
                pointIds[i] = IntUtil.parseOr(preprocessedIds[i],-1);
            }
            pointIds = ArrayUtil.resize(pointIds, i -> i != -1);
        }

        return ValueErrorTuple.value(
                new BranchEntry(start, includedNamespaces, includedTags, pointIds)
        );
    }

    private String[] trimResizeAndNullOnEmpty(String arr){
        String[] result = ArrayUtil.resize(
                ArrayUtil.forEach(
                        arr.split(entryMemberSubdivision),
                        String::trim
                ),
                e -> !e.isEmpty()
        );
        return result.length == 0 ? null : result;
    }

    private Exception updateDefinitions(){
        ValueErrorTuple<String,Exception> entryEndResult = secrets.getConfigurable(
                "MTPD-Entry-End"
        );
        if(entryEndResult.hasError()) return entryEndResult.error();
        entryEnd = entryEndResult.value();

        ValueErrorTuple<String,Exception> entryMemberSubdivisionResult = secrets.getConfigurable(
                "MTPD-Entry-Member-Subdivision"
        );
        if(entryMemberSubdivisionResult.hasError()) return entryMemberSubdivisionResult.error();
        entryMemberSubdivision = entryMemberSubdivisionResult.value();

        ValueErrorTuple<String,Exception> entrySubdivisionResult = secrets.getConfigurable(
                "MTPD-Entry-Subdivision"
        );
        if(entrySubdivisionResult.hasError()) return entrySubdivisionResult.error();
        entrySubdivision = entrySubdivisionResult.value();

        return null;
    }

    private ValueErrorTuple<String[],Exception> preflightChecks(String param){
        if(param == null)
            return ValueErrorTuple.error(new Exception(
                    "Context param is null... somehow. Well done."
            ));
        if(param.contains("&"))
            return ValueErrorTuple.error(new Exception(
                    "Only 1 query parameter (containing any amount of entries) allowed."
            ));
        if(param.isEmpty())
            return ValueErrorTuple.error(new Exception(
                    "Expected at least an empty entry declaration," +
                            " i.e. "+entrySubdivision+"<namespaces>"+entrySubdivision+"<includedTags>"+entrySubdivision+"<pointIds>"+entryEnd
            ));

        String[] entries = param.split(entryEnd);
        entries = ArrayUtil.resize(entries, e -> !e.isEmpty());

        if(entries.length == 0)
            return ValueErrorTuple.error(new Exception(
                    "No entries declared."
            ));

        Exception definitionsError = updateDefinitions();
        if(definitionsError != null)
            return ValueErrorTuple.error(definitionsError);

        return ValueErrorTuple.value(entries);
    }

}
