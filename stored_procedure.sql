USE `moviedb`;
DROP procedure IF EXISTS `add_movie`;

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

