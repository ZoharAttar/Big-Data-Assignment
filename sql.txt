-- Create MaximalDistance function

CREATE FUNCTION MaximalDistance()
RETURNS NUMERIC(4, 0)
AS
BEGIN
    DECLARE @max_distance NUMERIC(4, 0);
    SELECT @max_distance = MAX(ABS(m1.PROD_YEAR - m2.PROD_YEAR))
    FROM MediaItems m1
    JOIN MediaItems m2 ON m1.MID <> m2.MID;;
    RETURN @max_distance;
END;


-- Create SimCalculation function
CREATE FUNCTION SimCalculation(
@MID1 NUMERIC(11, 0), 
@MID2 NUMERIC(11, 0), 
@max_distance SMALLINT)
RETURNS REAL
AS
BEGIN
    DECLARE @distance NUMERIC(4, 0);
    DECLARE @similarity DECIMAL(4, 2);
    
    -- Calculate distance
    SELECT @distance = ABS(m1.PROD_YEAR - m2.PROD_YEAR)
    FROM MediaItems m1
    JOIN MediaItems m2 ON m1.MID = @MID1 AND m2.MID = @MID2;
    
    -- Calculate similarity
    SELECT @similarity = 1 - (@distance / @max_distance);
    RETURN @similarity;
END;

