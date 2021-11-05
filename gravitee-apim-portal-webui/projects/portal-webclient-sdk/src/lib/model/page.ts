/**
 * Gravitee.io Portal Rest API
 * API dedicated to the devportal part of Gravitee
 *
 * The version of the OpenAPI document: 3.13.0-SNAPSHOT
 * Contact: contact@graviteesource.com
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { PageConfiguration } from './pageConfiguration';
import { PageRevisionId } from './pageRevisionId';
import { Metadata } from './metadata';
import { PageMedia } from './pageMedia';
import { PageLinks } from './pageLinks';


export interface Page { 
    /**
     * Unique identifier of a page.
     */
    id: string;
    /**
     * Name of the page.
     */
    name: string;
    /**
     * Type of documentation.
     */
    type: Page.TypeEnum;
    /**
     * Order of the documentation page in its folder.
     */
    order: number;
    /**
     * Parent page. MAY be null.
     */
    parent?: string;
    /**
     * Last update date and time.
     */
    updated_at?: Date;
    configuration?: PageConfiguration;
    /**
     * list of media hash, attached to this page
     */
    media?: Array<PageMedia>;
    /**
     * Array of metadata about the page. This array is filled when the page has been fetched from a distant source (GitHub, GitLab, etc...).
     */
    metadata?: Array<Metadata>;
    _links?: PageLinks;
    /**
     * Only returned with *_/apis/{apiId}/pages/{pageId}* and *_/pages/{pageId}*. Need *include* query param to contain \'content\'.  The content of the page. 
     */
    content?: string;
    contentRevisionId?: PageRevisionId;
}
export namespace Page {
    export type TypeEnum = 'ASCIIDOC' | 'ASYNCAPI' | 'SWAGGER' | 'MARKDOWN' | 'FOLDER' | 'ROOT' | 'LINK';
    export const TypeEnum = {
        ASCIIDOC: 'ASCIIDOC' as TypeEnum,
        ASYNCAPI: 'ASYNCAPI' as TypeEnum,
        SWAGGER: 'SWAGGER' as TypeEnum,
        MARKDOWN: 'MARKDOWN' as TypeEnum,
        FOLDER: 'FOLDER' as TypeEnum,
        ROOT: 'ROOT' as TypeEnum,
        LINK: 'LINK' as TypeEnum
    };
}


