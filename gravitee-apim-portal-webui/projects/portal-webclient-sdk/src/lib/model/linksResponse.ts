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
import { CategorizedLinks } from './categorizedLinks';


export interface LinksResponse { 
    /**
     * Map of CategorizedLinks. Keys of the map can be: * aside * header * topfooter * footer 
     */
    slots?: { [key: string]: Array<CategorizedLinks>; };
}

