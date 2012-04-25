/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.domain.Avatar;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Provides database services for avatars.
 *
 * @author Sindre Mehus
 */
public class AvatarDao extends AbstractDao {

    private static final String COLUMNS = "id, name, created_date, mime_type, width, height, data";
    private final AvatarRowMapper rowMapper = new AvatarRowMapper();

    /**
     * Returns all system avatars.
     *
     * @return All system avatars.
     */
    public List<Avatar> getAllSystemAvatars() {
        String sql = "select " + COLUMNS + " from system_avatar";
        return query(sql, rowMapper);
    }

    /**
     * Returns the system avatar with the given ID.
     *
     * @param id The system avatar ID.
     * @return The avatar or <code>null</code> if not found.
     */
    public Avatar getSystemAvatar(int id) {
        String sql = "select " + COLUMNS + " from system_avatar where id=" + id;
        return queryOne(sql, rowMapper);
    }

    /**
     * Returns the custom avatar for the given user.
     *
     * @param username The username.
     * @return The avatar or <code>null</code> if not found.
     */
    public Avatar getCustomAvatar(String username) {
        String sql = "select " + COLUMNS + " from custom_avatar where username=?";
        return queryOne(sql, rowMapper, username);
    }

    /**
     * Sets the custom avatar for the given user.
     *
     * @param avatar   The avatar, or <code>null</code> to remove the avatar.
     * @param username The username.
     */
    public void setCustomAvatar(Avatar avatar, String username) {
        String sql = "delete from custom_avatar where username=?";
        update(sql, username);

        if (avatar != null) {
            update("insert into custom_avatar(" + COLUMNS + ", username) values(" + questionMarks(COLUMNS) + ", ?)",
                   null, avatar.getName(), avatar.getCreatedDate(), avatar.getMimeType(),
                   avatar.getWidth(), avatar.getHeight(), avatar.getData(), username);
        }
    }

    private static class AvatarRowMapper implements ParameterizedRowMapper<Avatar> {
        public Avatar mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Avatar(rs.getInt(1), rs.getString(2), rs.getTimestamp(3), rs.getString(4),
                              rs.getInt(5), rs.getInt(6), rs.getBytes(7));
        }
    }

}