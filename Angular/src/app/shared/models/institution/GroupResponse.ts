import {SimplifiedCourseResponse} from './SimplifiedCourseResponse';
import {SimplifiedProgramResponse} from './SimplifiedProgramResponse';

export interface GroupResponse {
    id?: string;
    name?: string;
    institutionID?: string;
    students?: string[];
    courses?: SimplifiedCourseResponse[];
    program?: string;
    simplifiedProgram?: SimplifiedProgramResponse;
}
