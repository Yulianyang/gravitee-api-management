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
import { OAuthClientSettings } from './oAuthClientSettings';
import { SimpleApplicationSettings } from './simpleApplicationSettings';


export interface ApplicationSettings { 
    app?: SimpleApplicationSettings;
    oauth?: OAuthClientSettings;
}

