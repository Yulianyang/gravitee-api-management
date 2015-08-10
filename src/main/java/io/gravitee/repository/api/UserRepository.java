/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.repository.api;

import java.util.Optional;
import java.util.Set;

import io.gravitee.repository.model.User;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 */
public interface UserRepository {

    /**
     * Create a {@link User}
     * 
     * @param usr user to create
     * @return User created
     */
	User create(User user);
	
	/**
	 * Find a {@link User} by name
	 * 
	 * @param username Name of the searched user
	 * @return Option user found
	 */
	Optional<User> findByUsername(String username);

	/**
	 * Find all {@link User}s
	 * 
	 * @return Users found
	 */
    Set<User> findAll();

	/**
	 * Find all {@link User} in a team
	 * 
	 * @param team name
	 * @return users found
	 */
    Set<User> findByTeam(String teamName);
}
