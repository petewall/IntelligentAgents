package edu.umn.kylepete;

import java.io.IOException;

import com.google.gson.stream.JsonReader;

public class RouteStep {

    // Values from https://github.com/Project-OSRM/osrm-backend/blob/master/data_structures/turn_instructions.hpp
    public enum Code {
        NoTurn, 
        GoStraight,
        TurnSlightRight,
        TurnRight,
        TurnSharpRight,
        UTurn,
        TurnSharpLeft,
        TurnLeft,
        TurnSlightLeft,
        ReachViaLocation,
        HeadOn,
        EnterRoundAbout,
        LeaveRoundAbout,
        StayOnRoundAbout,
        StartAtEndOfStreet,
        ReachedYourDestination,
        EnterAgainstAllowedDirection,
        LeaveAgainstAllowedDirection,
        InverseAccessRestrictionFlag,
        AccessRestrictionFlag,
        AccessRestrictionPenalty;
        
        public static Code fromIntValue(int value) {
            switch(value) {
            case 0: return NoTurn;
            case 1: return GoStraight;
            case 2: return TurnSlightRight;
            case 3: return TurnRight;
            case 4: return TurnSharpRight;
            case 5: return UTurn;
            case 6: return TurnSharpLeft;
            case 7: return TurnLeft;
            case 8: return TurnSlightLeft;
            case 9: return ReachViaLocation;
            case 10: return HeadOn;
            case 11: return EnterRoundAbout;
            case 12: return LeaveRoundAbout;
            case 13: return StayOnRoundAbout;
            case 14: return StartAtEndOfStreet;
            case 15: return ReachedYourDestination;
            case 16: return EnterAgainstAllowedDirection;
            case 17: return LeaveAgainstAllowedDirection;
            case 127: return InverseAccessRestrictionFlag;
            case 128: return AccessRestrictionFlag;
            case 129: return AccessRestrictionPenalty;
            }
            return null;
        }
    };
    
    public enum Direction {
        N, S, E, W, NE, SE, SW, NW;
    };

    public Code code;
    public String name;
    public int length; // meters
    public int positionIndex;
    public double time;
    public String lengthString;
    public Direction direction;
    public double azimuth;
    public int mode;

    public static RouteStep fromJsonReader(JsonReader reader) throws IOException {
        RouteStep step = new RouteStep();
        reader.beginArray();
        step.code = Code.fromIntValue(Integer.parseInt(reader.nextString()));
        step.name = reader.nextString();
        step.length = reader.nextInt();
        step.positionIndex = reader.nextInt();
        step.time = reader.nextDouble();
        step.lengthString = reader.nextString();
        step.direction = Direction.valueOf(reader.nextString());
        step.azimuth = reader.nextDouble();
        step.mode = reader.nextInt();

        reader.endArray();
        return null;
    }
}
