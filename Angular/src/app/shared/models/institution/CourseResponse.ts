import {CoursePostResponse} from './CoursePostResponse';
import {Quiz} from '../Quiz';
import {SafeUrl} from '@angular/platform-browser';

export interface CourseResponse {
    imageSrc: SafeUrl;
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
