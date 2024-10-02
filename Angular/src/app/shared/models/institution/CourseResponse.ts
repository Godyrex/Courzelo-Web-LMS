import {CoursePostResponse} from './CoursePostResponse';
import {Quiz} from '../Quiz';

export interface CourseResponse {
    id?: string;
    name?: string;
    description?: string;
    module?: string;
    credit?: number;
    teacher?: string;
    group?: string;
    institutionID?: string;
    created?: Date;
    posts?: CoursePostResponse[];
    quizzes?: Quiz[];
}
