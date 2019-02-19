USE `moviedb`;
DROP procedure IF EXISTS `add_movie`;
DROP procedure IF EXISTS `add_mains`;
DROP procedure IF EXISTS `add_mains_genre`;
DROP procedure IF EXISTS `add_actor`;
DROP procedure IF EXISTS `add_to_sim`;

CREATE TABLE employees
(
	email varchar(50) primary key,
	password varchar(20) not null,
	fullname varchar(100)
)


DELIMITER $$
USE `moviedb`$$
CREATE DEFINER=`mytestuser`@`localhost` PROCEDURE `add_movie`(IN title VARCHAR(100),IN year INT(11), IN director VARCHAR(100), IN star_name VARCHAR(100),IN star_birth_year INT(11), IN genre_name VARCHAR(32))
BEGIN
	DECLARE star_id VARCHAR(10);
    DECLARE genre_id INT(11);
    DECLARE movie_id VARCHAR(10);
    
	select id into star_id from stars where stars.name = star_name limit 1;
    select id into genre_id from genres where genres.name = genre_name limit 1;
    select id into movie_id from movies where movies.title = title and movies.year = year and movies.director = director;
    IF movie_id IS NULL #if the movie doesnt exist
    THEN
		(select ifnull
		(concat('tt',LPAD(
			(substring_index
				(max(id),'tt',-1) + 1),7,'0')),1) into movie_id from movies);
		insert into movies (id,title,year,director) VALUES (movie_id,title,year,director);
    
		IF star_id IS NULL #the given star does not exist, create and insert into stars_in_movies
		THEN 
			(select ifnull
			(concat('nm',LPAD(
				(substring_index
					(max(id),'nm',-1) + 1),7,'0')),1) into star_id from stars);
			insert into stars (id,name,birthyear) VALUES (star_id,star_name,star_birth_year);
			insert into stars_in_movies (starId,movieId) VALUES(star_id,movie_id);
		ELSE #the star already exists so just insert it into the stars_in_movies
			insert into stars_in_movies (starId,movieId) VALUES(star_id,movie_id);
		END IF;
	 #create the star and link it to the movie (stars_in_movies)
		IF genre_id IS NULL # genre does not exist, create genre then insert into genres_in_movies
		THEN
			select max(id) + 1 into genre_id from genres;
			insert into genres (id,name) VALUES(genre_id,genre_name);
			insert into genres_in_movies (genreId,movieId) VALUES(genre_id,movie_id);
		ELSE #genre already exists so insert it into the genres_in_movies
			insert into genres_in_movies (genreId,movieId) VALUES(genre_id,movie_id);
		END IF;#create the genre and link it to the movie (genre_in_movies)
	ELSE
		SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Movie Exists in Database';
	END IF;
END$$
DELIMITER ;


DELIMITER $$
USE `moviedb`$$ 
CREATE DEFINER=`mytestuser`@`localhost` PROCEDURE `add_mains`(IN m_id VARCHAR(10), IN m_title varchar(100), IN m_year INT(11), IN m_director varchar(100))
Begin
		DECLARE movie_id VARCHAR(10);
        #determine if movie exists in the db
		SELECT id into movie_id from movies where movies.id = m_id;
        
        IF movie_id IS NULL THEN
			insert into movies(id,title,year,director) VALUES(m_id,m_title,m_year,m_director);
        
        #ELSE 
			#SIGNAL SQLSTATE '45000'
            #SET MESSAGE_TEXT = "Movie Exists Already!";
        
        END IF;
END $$ 
DELIMITER ;

DELIMITER $$
USE `moviedb` $$
CREATE DEFINER=`mytestuser`@`localhost` PROCEDURE `add_mains_genre`(IN movie_id VARCHAR(10), IN genre_name VARCHAR(32))
BEGIN
	DECLARE genre_id INT(11);
    
	select id into genre_id from genres where genres.name = genre_name limit 1;
    
    IF genre_id IS NULL THEN
        SELECT max(id) + 1 into genre_id from genres;
		insert into genres(id,name) VALUES (genre_id,genre_name);
    END IF;
    insert into genres_in_movies(genreId,movieId) VALUES(genre_id,movie_id);
END$$
DELIMITER ;

DELIMITER $$
USE `moviedb` $$
CREATE DEFINER=`mytestuser`@`localhost` PROCEDURE `add_to_sim`(IN actor_name VARCHAR(100), IN movie_id VARCHAR(10))
BEGIN
	DECLARE m_id VARCHAR(10);
    DECLARE a_id VARCHAR(10);
    
    select id into m_id from movies where movies.id = movie_id;
    select id into a_id from stars where stars.name = actor_name limit 1;
    IF a_id IS NULL THEN
		SIGNAL SQLSTATE '45000'
        SET message_text = 'Actor does not exist in database';
	ELSE IF m_id IS NULL THEN
		SIGNAL sqlstate '45000'
        SET message_text = 'Movie does not exist in database';
    ELSE
		insert into stars_in_movies(starId,movieId) VALUES(a_id,m_id);
        
	END IF; 
    END IF;
END$$
DELIMITER ;


DELIMITER $$
USE `moviedb` $$
CREATE DEFINER=`mytestuser`@`localhost` PROCEDURE `add_actor`(IN actor_name VARCHAR(10), IN birth_year INT(11))
BEGIN
	DECLARE a_id VARCHAR(10);
    
    select id into a_id from stars where stars.name = actor_name limit 1;
	IF a_id IS NULL #the given star does not exist, create and insert into stars_in_movies
	THEN 
		(select ifnull
		(concat('nm',LPAD(
			(substring_index
				(max(id),'nm',-1) + 1),7,'0')),1) into a_id from stars);
		insert into stars (id,name,birthyear) VALUES (a_id,actor_name,birth_year);

	END IF;
END$$
DELIMITER ;

