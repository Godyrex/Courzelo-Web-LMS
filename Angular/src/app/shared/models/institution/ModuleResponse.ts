import {AssessmentRequest} from './AssessmentRequest';

export interface ModuleResponse {
    courseTeacher?: string;
    id?: string;
    name?: string;
    description?: string;
    skills?: string[];
    semester?: string;
    duration?: string;
    credit?: number;
    assessments?: AssessmentRequest[];
    institutionID?: string;
    program?: string;
    courseCreated?: boolean;
    courseID?: string;
}
